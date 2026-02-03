package com.ajex.invoice.staging.kafka.invoice;

import com.ajex.invoice.staging.dto.OracleInvoiceStatusResponse;
import com.ajex.invoice.staging.integration.AIMSCommonService;
import com.ajex.invoice.staging.service.InvoiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class OracleInvoiceStatusConsumer {

    private final ObjectMapper objectMapper;
    private final AIMSCommonService aimsCommonService;
    private final InvoiceService invoiceService;

    @KafkaListener(topics = "${aims.invoice.result.topic:ajex-invoice.aims.result.v1}")
    public void oracleInvoiceListener(String sOracleInvoice) {
        log.info("Received from invoice-management :: {}", sOracleInvoice);
        try {
            OracleInvoiceStatusResponse oracleInvoiceStatusResponse = objectMapper.readValue(sOracleInvoice, OracleInvoiceStatusResponse.class);
            aimsCommonService.updateOracleInvoiceResponse(oracleInvoiceStatusResponse);
            invoiceService.updateInvoiceStageStatusFromOracle(oracleInvoiceStatusResponse);
        } catch (JsonProcessingException e) {
            log.error("Exception while consuming :: {}", sOracleInvoice, e);
        }

    }

}