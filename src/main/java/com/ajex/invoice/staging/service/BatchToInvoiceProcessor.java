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

import static com.ajex.invoice.staging.constant.InvoiceDetailStatus.INVOICE_STAGE_DONE;
import static com.ajex.invoice.staging.constant.InvoiceStagingConstant.LOG_UPDATE_STATUS;

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

            List<LandFreightInvoiceDetail> landFreightInvoiceDetails;
            if (batch.waybillNos().isEmpty()) {
                landFreightInvoiceDetails = invoiceService
                        .findAllByStatus(InvoiceDetailStatus.INVOICE_STAGE_PENDING.getValue());
            } else {
                landFreightInvoiceDetails = invoiceService
                        .findAllByStatusAndWaybillNoIn(InvoiceDetailStatus.INVOICE_STAGE_PENDING.getValue(),
                                batch.waybillNos());
            }
            if (!landFreightInvoiceDetails.isEmpty()) {
                postAndSaveDBInvoice(batch, landFreightInvoiceDetails);
                context.commit();
                log.info("***** END BatchToInvoiceProcessor done for waybillNos: {}", record.value().waybillNos());
            } else {
                log.info("***** END No pending invoice details found for waybillNos: {}", record.value().waybillNos());
            }
        } catch (Exception ex) {
            log.info("BatchToInvoiceProcessor Error in process data to AIMS , {}", record.value().waybillNos(), ex);
            throw new RuntimeException("Failed to push invoice", ex);
        }
    }

    private void postAndSaveDBInvoice(BatchEvent batch, List<LandFreightInvoiceDetail> landFreightInvoiceDetails) {
        for (LandFreightInvoiceDetail landFreightInvoiceDetail : landFreightInvoiceDetails) {
            invoiceService.postInvoiceData(List.of(landFreightInvoiceDetail.getAimsInvoiceData()));
            landFreightInvoiceDetail.setStatus(INVOICE_STAGE_DONE);
        }
        invoiceService.saveLandFreightInvoiceDetails(landFreightInvoiceDetails);
        log.info(LOG_UPDATE_STATUS, INVOICE_STAGE_DONE);
    }

}
