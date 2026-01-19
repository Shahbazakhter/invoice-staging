package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.dto.BatchEvent;
import com.ajex.invoice.staging.dto.RowEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class RowToBatchAggregatorProcessor
        implements Processor<String, RowEvent, String, BatchEvent> {

    private ProcessorContext<String, BatchEvent> context;
    private KeyValueStore<String, AggregationState> store;

    @Override
    public void init(ProcessorContext<String, BatchEvent> context) {
        this.context = context;
        this.store = context.getStateStore("aggregation-store");

        // TIME trigger every minute
        this.context.schedule(Duration.ofMinutes(1), PunctuationType.WALL_CLOCK_TIME, this::onPunctuate);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    void postConstruct() {
        objectMapper.registeredModules();
    }

    @Override
    public void process(Record<String, RowEvent> record) {
        log.info("Processing started with key: {}", record.key());
        try {
            String key = record.key();
            RowEvent event = record.value();

            AggregationState state = store.get(key);

            if (state == null) {
                state = new AggregationState();
            }
            state.add(event);

            // SIZE trigger (20 rows)
            if (state.sizeReached()) {
                emitBatch(key, state, "SIZE");
                store.delete(key);
            } else {
                store.put(key, state);
            }
            log.info("Processing done with key: {}", record.key());
        } catch (Exception e) {
            log.error("Exception occurred processing record: ", e);
            throw new RuntimeException(e);
        }
    }

    private void onPunctuate(long timestamp) {
        log.info("Punctuation started at {}", Instant.ofEpochMilli(timestamp));
        try {
            Instant now = Instant.ofEpochMilli(timestamp);

            try (KeyValueIterator<String, AggregationState> it = store.all()) {

                while (it.hasNext()) {
                    KeyValue<String, AggregationState> entry = it.next();
                    AggregationState state = entry.value;

                    if (state.timeExceeded(now)) {
                        emitBatch(entry.key, state, "TIME");
                        store.delete(entry.key);
                    }
                }
            }
            log.debug("Punctuation done");
        } catch (Exception e) {
            log.error("Exception occurred during punctuation: ", e);
            throw new RuntimeException(e);
        }
    }

    private void emitBatch(String key,
                           AggregationState state,
                           String trigger) {
        try {
            log.info("Emitting batch started key: {} with {} rows due to {} trigger",
                    key, state.getRowIds().size(), trigger);
            BatchEvent batch = new BatchEvent(
                    UUID.randomUUID().toString(),
                    key,
                    state.getRowIds(),
                    trigger
            );
            context.forward(new Record<>(key, batch, System.currentTimeMillis()));
            log.debug("Emitting batch done key: {}", key);
        } catch (Exception e) {
            log.error("Exception occurred emitting batch: ", e);
            throw new RuntimeException(e);
        }
    }

}
