package com.ajex.invoice.staging.kafka.stage;

import com.ajex.invoice.staging.dto.RowEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class AimsLandFreightInvoiceProducer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${aims.invoice-staging.row-input.topic:aims.invoice-staging.row-input.topic.v1}")
    private String rowInputTopic;

    public void push(RowEvent rowEvents) {
        try {
            log.info("Received kafka AIMS Invoice Staging message {}", rowEvents);
            String serializedEvent = objectMapper.writeValueAsString(rowEvents);
            kafkaTemplate.send(rowInputTopic, rowEvents.aggregationKey(), serializedEvent);
        } catch (Exception e) {
            log.error("Error while push message", e);
        }
    }

}
