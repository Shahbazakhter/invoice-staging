package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.constant.InvoiceDetailStatus;
import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.BatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BatchToInvoiceProcessor implements Processor<String, BatchEvent, Void, Void> {

    private ProcessorContext<Void, Void> context;

    private final InvoiceService invoiceService;

    public BatchToInvoiceProcessor(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        this.context = context;
    }

    @Override
    public void process(Record<String, BatchEvent> record) {
        try {
            log.info("BatchToInvoiceProcessor process event for waybillNos: {}", record.value().waybillNos());
            BatchEvent batch = record.value();

            List<LandFreightInvoiceDetail> landFreightInvoiceDetails = invoiceService
                    .findAllByStatusAndWaybillNoIn(InvoiceDetailStatus.INVOICE_STAGE_PENDING.getValue(),
                    batch.waybillNos());

            for (LandFreightInvoiceDetail landFreightInvoiceDetail : landFreightInvoiceDetails) {
                invoiceService.postInvoiceData(List.of(landFreightInvoiceDetail.getAimsInvoiceData()));
                landFreightInvoiceDetail.setStatus(InvoiceDetailStatus.INVOICE_STAGE_DONE);
            }

            invoiceService.saveLandFreightInvoiceDetails(landFreightInvoiceDetails);
            context.commit();
        } catch (Exception ex) {
            log.info("Error in pushing invoice data to AIMS , {}", record.value().waybillNos(), ex);
            throw new RuntimeException("Failed to push invoice", ex);
        }
    }

}
