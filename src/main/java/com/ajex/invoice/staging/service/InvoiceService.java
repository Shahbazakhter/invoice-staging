package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.constant.BusinessLine;
import com.ajex.invoice.staging.document.InvoiceDetail;
import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.*;
import com.ajex.invoice.staging.exception.InvoiceStagingException;
import com.ajex.invoice.staging.integration.AIMSCommonService;
import com.ajex.invoice.staging.kafka.invoice.AimsLandFreightInvoiceMWProducer;
import com.ajex.invoice.staging.kafka.stage.AimsLandFreightInvoiceProducer;
import com.ajex.invoice.staging.repository.InvoiceDetailRepository;
import com.ajex.invoice.staging.repository.LandFreightInvoiceDetailRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.ajex.invoice.staging.constant.InvoiceDetailStatus.*;
import static com.ajex.invoice.staging.constant.InvoiceStagingConstant.LOG_UPDATE_STATUS;
import static com.ajex.invoice.staging.constant.InvoiceStagingConstant.getUTCInstant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final LandFreightInvoiceDetailRepository landFreightInvoiceDetailRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final AimsLandFreightInvoiceProducer aimsLandFreightProducer;
    private final AimsLandFreightInvoiceMWProducer aimsLandFreightInvoiceMWProducer;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // behaves like "virtual thread pool"
    private final AIMSCommonService aimsCommonService;

    @Value("${aims.invoice-staging.row-input.topic:aims.invoice-staging.row-input.topic.v1}")
    private String rowInputTopic;

    @Value("${aims.invoice-staging.batch-output.topic:aims.invoice-staging.batch-output.topic.v1}")
    private String batchOutputTopic;

    public List<LandFreightInvoiceDetail> getAllInvoices(InvoiceFilterRequest invoiceFilterRequest) {

        return invoiceDetailRepository.filter(invoiceFilterRequest, 1, 5000);
    }

    public void stageForPush(@Valid List<String> waybillNos) {
        log.info("***** START Push to Oracle initiated for waybill numbers: {}", waybillNos);
        List<LandFreightInvoiceDetail> landFreightInvoiceDetail =
                landFreightInvoiceDetailRepository
                        .findAllByStatusInAndWaybillNoIn(List
                                .of(INVOICE_STAGE.getValue(), ERROR.getValue()), waybillNos);

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
        log.info(LOG_UPDATE_STATUS, INVOICE_STAGE_PENDING);

        for (RowEvent rowEvent : rowEvents) {
            executorService.execute(() -> aimsLandFreightProducer.push(rowEvent));
        }

    }

    public void stageInvoices(List<AimsInvoiceData> invoiceDatas) {
        List<LandFreightInvoiceDetail> landFreightInvoiceDetails = new ArrayList<>();
        List<String> waybillNos = invoiceDatas.stream().map(AimsInvoiceData::getWaybillNo).distinct().toList();
        Map<String, ObjectId> objectIdByWaybillNo = landFreightInvoiceDetailRepository.findAllByWaybillNoIn(waybillNos)
                .stream().collect(Collectors.toMap(InvoiceDetail::getWaybillNo, InvoiceDetail::getId));
        invoiceDatas.forEach(invoiceData -> {

            LandFreightInvoiceDetail landFreightInvoiceDetail = new LandFreightInvoiceDetail();
            if(objectIdByWaybillNo.containsKey(invoiceData.getWaybillNo())){
                landFreightInvoiceDetail.setId(objectIdByWaybillNo.get(invoiceData.getWaybillNo()));
            }
            landFreightInvoiceDetail.setAimsInvoiceData(invoiceData);
            landFreightInvoiceDetail.setWaybillNo(invoiceData.getWaybillNo());
            landFreightInvoiceDetail.setBusinessLine(BusinessLine.LAND_FREIGHT);
            landFreightInvoiceDetail.setStatus(INVOICE_STAGE);
            landFreightInvoiceDetail.setInsertionTimestamp(getUTCInstant().toString());
            landFreightInvoiceDetails.add(landFreightInvoiceDetail);
        });
        landFreightInvoiceDetailRepository.saveAll(landFreightInvoiceDetails);
    }

    public void postInvoiceData(List<AimsInvoiceData> aimsInvoiceData) {
        aimsLandFreightInvoiceMWProducer.postInvoiceData(aimsInvoiceData);
    }

    public List<LandFreightInvoiceDetail> findAllByStatus(String status) {
        return landFreightInvoiceDetailRepository.findAllByStatus(status);
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

    public void updateInvoiceStageStatusFromOracle(OracleInvoiceStatusResponse oracleInvoiceStatusResponse) {
        List<String> successWaybillNos = oracleInvoiceStatusResponse
                .getSuccessfulInvoices().stream()
                .map(InvoiceStatus::getWaybillNumber)
                .toList();

        List<String> failedWaybillNos = oracleInvoiceStatusResponse
                .getFailedInvoices().stream()
                .map(InvoiceStatus::getWaybillNumber)
                .toList();

        List<LandFreightInvoiceDetail> successInvoiceDetail = landFreightInvoiceDetailRepository
                .findAllByStatusAndWaybillNoIn(INVOICE_STAGE_DONE.getValue(), successWaybillNos);

        if (!successInvoiceDetail.isEmpty()) {
            successInvoiceDetail.forEach(landFreightInvoiceDetail -> {
                landFreightInvoiceDetail.setStatus(PUSHED_TO_ORACLE);
                oracleInvoiceStatusResponse.getFailedInvoices().stream().findFirst()
                        .ifPresent(success -> {
                            landFreightInvoiceDetail.setTransactionDate(success.getTransactionDate());
                            landFreightInvoiceDetail.setTransactionId(success.getTransactionId());
                        });
            });
            landFreightInvoiceDetailRepository.saveAll(successInvoiceDetail);
            log.info(LOG_UPDATE_STATUS, PUSHED_TO_ORACLE);
        }

        List<LandFreightInvoiceDetail> failedInvoiceDetails = landFreightInvoiceDetailRepository
                .findAllByStatusAndWaybillNoIn(INVOICE_STAGE_DONE.getValue(), failedWaybillNos);

        if (!failedInvoiceDetails.isEmpty()) {
            failedInvoiceDetails.forEach(landFreightInvoiceDetail -> {
                landFreightInvoiceDetail.setStatus(ERROR);
                oracleInvoiceStatusResponse.getFailedInvoices().stream().findFirst()
                        .ifPresent(failed -> landFreightInvoiceDetail.getErrorList()
                                .add(new InvoiceError(failed.getResponseCode() + ": " + failed.getResponseMessage(),
                                        Instant.now().toEpochMilli())));

            });
            landFreightInvoiceDetailRepository.saveAll(failedInvoiceDetails);
            log.info(LOG_UPDATE_STATUS, ERROR);
        }
    }

    public void allowManifesterEdit(List<String> waybillNos) {
        aimsCommonService.revokeManifesterConfirmation(waybillNos);
    }

    public void revalidateAgreement(List<String> waybillNos) {
        aimsCommonService.recalculateAgreement(waybillNos);
    }

}
