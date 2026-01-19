package com.ajex.invoice.staging.config;

import com.ajex.invoice.staging.dto.BatchEvent;
import com.ajex.invoice.staging.dto.RowEvent;
import com.ajex.invoice.staging.exception.ResilientStreamsExceptionHandler;
import com.ajex.invoice.staging.service.AggregationState;
import com.ajex.invoice.staging.service.BatchToInvoiceProcessor;
import com.ajex.invoice.staging.service.InvoiceService;
import com.ajex.invoice.staging.service.RowToBatchAggregatorProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaStreamsConfig {

    public static final String ROW_AGGREGATOR_CONFIG_BEAN_NAME = "defaultKafkaRowAggregatorConfig";
    public static final String BATCH_INVOICE_CONFIG_BEAN_NAME = "defaultKafkaBatchInvoiceConfig";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${aims.invoice-staging.row-input.topic:aims.invoice-staging.row-input.topic.v1}")
    private String rowInputTopic;

    @Value("${aims.invoice-staging.batch-output.topic:aims.invoice-staging.batch-output.topic.v1}")
    private String batchOutputTopic;

    private final InvoiceService invoiceService;

    /**
     *
     */
    @Bean(name = ROW_AGGREGATOR_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration rowAggregatorStreamsConfig() {

        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "row-aggregator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG,
                StreamsConfig.EXACTLY_ONCE_V2);

        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public Topology aggregatorTopology() {
        Topology topology = new Topology();

        topology.addSource("row-source",
                        Serdes.String().deserializer(),
                        new JacksonJsonSerde<>(RowEvent.class).deserializer(),
                        rowInputTopic)

                .addProcessor("batch-aggregator",
                        RowToBatchAggregatorProcessor::new,
                        "row-source")

                .addStateStore(Stores.keyValueStoreBuilder(
                                Stores.persistentKeyValueStore("aggregation-store"),
                                Serdes.String(),
                                new JacksonJsonSerde<>(AggregationState.class)),
                        "batch-aggregator")

                .addSink("batch-sink",
                        batchOutputTopic,
                        Serdes.String().serializer(),// key
                        new JacksonJsonSerde<>(BatchEvent.class).serializer(), // value
                        "batch-aggregator");

        return topology;
    }

    @Bean
    public KafkaStreams rowAggregatorKafkaStreams(@Qualifier("aggregatorTopology") Topology aggregatorTopology,
                                                  @Qualifier(ROW_AGGREGATOR_CONFIG_BEAN_NAME) KafkaStreamsConfiguration rowAggregatorStreamsConfig) {
        KafkaStreams streams = new KafkaStreams(aggregatorTopology, rowAggregatorStreamsConfig.asProperties());
        streams.setUncaughtExceptionHandler(new ResilientStreamsExceptionHandler());
        streams.start();
        return streams;
    }

    /**
     *
     */
    @Bean(name = BATCH_INVOICE_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration batchInvoiceStreamsConfig() {

        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "batch-invoice-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG,
                StreamsConfig.EXACTLY_ONCE_V2);

        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams/batch-invoice");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public Topology batchInvoiceTopology() {
        Topology topology = new Topology();

        topology.addSource("batch-source",
                        Serdes.String().deserializer(),
                        new JacksonJsonSerde<>(BatchEvent.class).deserializer(),
                        batchOutputTopic)

                .addProcessor("batch-invoice-aggregator",
                        () -> new BatchToInvoiceProcessor(invoiceService),
                        "batch-source");
        return topology;
    }

    @Bean
    public KafkaStreams invoiceBatchStreams(@Qualifier("batchInvoiceTopology") Topology batchInvoiceTopology,
                                            @Qualifier(BATCH_INVOICE_CONFIG_BEAN_NAME) KafkaStreamsConfiguration batchInvoiceStreamsConfig) {
        KafkaStreams streams = new KafkaStreams(batchInvoiceTopology, batchInvoiceStreamsConfig.asProperties());
        streams.setUncaughtExceptionHandler(new ResilientStreamsExceptionHandler());
        streams.start();
        return streams;
    }

}
