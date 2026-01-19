package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.constant.BusinessLine;
import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.AimsInvoiceData;
import com.ajex.invoice.staging.dto.RowEvent;
import com.ajex.invoice.staging.exception.InvoiceStagingException;
import com.ajex.invoice.staging.kafka.invoice.AimsLandFreightInvoiceMWProducer;
import com.ajex.invoice.staging.kafka.stage.AimsLandFreightInvoiceProducer;
import com.ajex.invoice.staging.repository.LandFreightInvoiceDetailRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.ajex.invoice.staging.constant.InvoiceDetailStatus.INVOICE_STAGE;
import static com.ajex.invoice.staging.constant.InvoiceDetailStatus.INVOICE_STAGE_PENDING;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final LandFreightInvoiceDetailRepository landFreightInvoiceDetailRepository;
    private final AimsLandFreightInvoiceProducer aimsLandFreightProducer;
    private final AimsLandFreightInvoiceMWProducer aimsLandFreightInvoiceMWProducer;

    @Value("${aims.invoice-staging.row-input.topic:aims.invoice-staging.row-input.topic.v1}")
    private String rowInputTopic;

    @Value("${aims.invoice-staging.batch-output.topic:aims.invoice-staging.batch-output.topic.v1}")
    private String batchOutputTopic;

    public List<LandFreightInvoiceDetail> getAllInvoices(String status,
                                                         String businessLine) {
        return landFreightInvoiceDetailRepository.findByStatusAndBusinessLine(status,
                businessLine);
    }

    public void stageForPush(@Valid List<String> waybillNos) {

        List<LandFreightInvoiceDetail> landFreightInvoiceDetail =
                landFreightInvoiceDetailRepository.findAllByStatusAndWaybillNoIn(INVOICE_STAGE.getValue(), waybillNos);

        if (landFreightInvoiceDetail.isEmpty()) {
            throw new InvoiceStagingException("Invoice Stage not found for waybill numbers: " + waybillNos, 400);
        }

        List<RowEvent> rowEvents = new ArrayList<>();
        Instant now = Instant.now();
        String uuid = UUID.randomUUID().toString();
        landFreightInvoiceDetail.forEach(ldFreightInvoiceDetail ->
        {
            ldFreightInvoiceDetail.setStatus(INVOICE_STAGE_PENDING);
            rowEvents.add(new RowEvent(uuid, ldFreightInvoiceDetail.getWaybillNo(), now));
        });
        landFreightInvoiceDetailRepository.saveAll(landFreightInvoiceDetail);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (RowEvent rowEvent : rowEvents) {
                executor.execute(() -> aimsLandFreightProducer.push(rowEvent));
            }
        }
        for (RowEvent rowEvent : rowEvents) {

            aimsLandFreightProducer.push(rowEvent);
        }
    }

    public void stageInvoices(List<AimsInvoiceData> invoiceDatas) {
        List<LandFreightInvoiceDetail> landFreightInvoiceDetails = new ArrayList<>();
        invoiceDatas.forEach(invoiceData -> {

            LandFreightInvoiceDetail landFreightInvoiceDetail = new LandFreightInvoiceDetail();
            landFreightInvoiceDetail.setAimsInvoiceData(invoiceData);
            landFreightInvoiceDetail.setWaybillNo(invoiceData.getWaybillNo());
            landFreightInvoiceDetail.setBusinessLine(BusinessLine.LAND_FREIGHT);
            landFreightInvoiceDetail.setStatus(INVOICE_STAGE);
            landFreightInvoiceDetails.add(landFreightInvoiceDetail);
        });
        landFreightInvoiceDetailRepository.saveAll(landFreightInvoiceDetails);
    }

    public void postInvoiceData(List<AimsInvoiceData> aimsInvoiceData) {
        aimsLandFreightInvoiceMWProducer.postInvoiceData(aimsInvoiceData);
    }

    public List<LandFreightInvoiceDetail> findAllByWaybillNoIn(List<String> waybillNos) {
        return landFreightInvoiceDetailRepository.findAllByWaybillNoIn(waybillNos);
    }

    public List<LandFreightInvoiceDetail> findAllByStatusAndWaybillNoIn(String status, List<String> waybillNos) {
        return landFreightInvoiceDetailRepository.findAllByStatusAndWaybillNoIn(status, waybillNos);
    }

    public void saveLandFreightInvoiceDetails(List<LandFreightInvoiceDetail> allByWaybillNoIn) {
        landFreightInvoiceDetailRepository.saveAll(allByWaybillNoIn);
    }

}
