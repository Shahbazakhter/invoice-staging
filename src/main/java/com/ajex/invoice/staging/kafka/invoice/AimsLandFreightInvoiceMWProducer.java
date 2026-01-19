package com.ajex.invoice.staging.kafka.invoice;

import com.ajex.invoice.staging.dto.AimsInvoiceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
@RequiredArgsConstructor
public class AimsLandFreightInvoiceMWProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${aims.invoice.request.topic:ajex-invoice.aims.request.v1}")
    private String oracleRequestTopic;

    public void postInvoiceData(List<AimsInvoiceData> aimsInvoiceData) {
        List<String> waybillNos = aimsInvoiceData.stream().map(AimsInvoiceData::getWaybillNo).toList();
        log.info("AimsLandFreightInvoiceMWProducer postInvoiceData {}", waybillNos);
        log.debug("Topic name {}", oracleRequestTopic);

        String sInvoiceDtoList = objectMapper.writeValueAsString(aimsInvoiceData);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(oracleRequestTopic, sInvoiceDtoList);

        handle(future, sInvoiceDtoList);
        log.info("AimsLandFreightInvoiceMWProducer postInvoiceData completed for {}", waybillNos);
    }

    private void handle(CompletableFuture<SendResult<String, Object>> future, Object object) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Unable to send message=[{}] due to : {}", object, ex.getMessage(), ex);
            } else {
                log.info("Sent message=[{}] with offset=[{}]",
                        object,
                        result.getRecordMetadata().offset());
            }
        });
    }

}
