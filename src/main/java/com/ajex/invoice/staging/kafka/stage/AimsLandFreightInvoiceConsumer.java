package com.ajex.invoice.staging.kafka.stage;

import com.ajex.invoice.staging.dto.AimsInvoiceData;
import com.ajex.invoice.staging.service.InvoiceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.ajex.invoice.staging.constant.InvoiceDetailStatus.INVOICE_STAGE;
import static com.ajex.invoice.staging.constant.InvoiceStagingConstant.LOG_UPDATE_STATUS;

@Component
@Slf4j
@RequiredArgsConstructor
public class AimsLandFreightInvoiceConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InvoiceService invoiceService;

    @PostConstruct
    void postConstruct() {
        objectMapper.registeredModules();
    }

    @KafkaListener(topics = {"${aims.invoice-staging.request.topic:aims.invoice-staging.request.topic.v1}"},
            groupId = "${spring.kafka.consumer.group-id}")
    public void listenToAimsInvoiceStaging(String message) {
        try {
            log.info("AIMS Invoice Staging message {}", message);
            List<AimsInvoiceData> invoiceData = objectMapper.readValue(message, new TypeReference<>() {
            });
            if (invoiceData != null && !invoiceData.isEmpty()) {
                invoiceService.stageInvoices(invoiceData);
                log.info(LOG_UPDATE_STATUS, INVOICE_STAGE);
            }
        } catch (Exception e) {
            log.error("Exception occurred listenToAimsInvoiceStaging", e);
        }
    }

}
