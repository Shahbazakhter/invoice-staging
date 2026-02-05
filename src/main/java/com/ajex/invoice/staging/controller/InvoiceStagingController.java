package com.ajex.invoice.staging.controller;

import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.CommonResponse;
import com.ajex.invoice.staging.dto.InvoiceFilterRequest;
import com.ajex.invoice.staging.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/invoices-staging")
@RequiredArgsConstructor
@Slf4j
public class InvoiceStagingController {

    private final InvoiceService invoiceService;

    /**
     * TODO Add Pagination
     */
    @GetMapping
    public List<LandFreightInvoiceDetail> getAllInvoiceStages(
            @RequestParam(value = "businessUnits", required = false) List<String> businessUnits,
            @RequestParam(value = "customerSubAccounts", required = false) List<String> customerSubAccounts,
            @RequestParam(value = "waybillNos", required = false) List<String> waybillNos,
            @RequestParam(value = "statuses", required = false) List<String> statuses,
            @RequestParam(defaultValue = "land_freight", required = false) String businessLine) {
        InvoiceFilterRequest invoiceFilterRequest = new InvoiceFilterRequest();
        invoiceFilterRequest.setBusinessUnits(businessUnits);
        invoiceFilterRequest.setBusinessLine(businessLine);
        invoiceFilterRequest.setCustomerSubAccounts(customerSubAccounts);
        invoiceFilterRequest.setWaybillNos(waybillNos);
        invoiceFilterRequest.setStatuses(statuses);
        return invoiceService.getAllInvoices(invoiceFilterRequest);
    }

    @PostMapping("/push")
    public CommonResponse<String> stagingForPush(@Valid @RequestBody List<String> waybillNos) {
        invoiceService.stageForPush(waybillNos);
        return new CommonResponse<>("Push to Oracle Initiated", 200);
    }

    @PutMapping("/allow-edit")
    public CommonResponse<String> allowManifesterEdit(@Valid @RequestBody List<String> waybillNos) {
        invoiceService.allowManifesterEdit(waybillNos);
        return new CommonResponse<>("Manifester confirmation is revoked", 200);
    }

    @PutMapping("/revalidate")
    public CommonResponse<String> revalidateAgreement(@Valid @RequestBody List<String> waybillNos) {
        invoiceService.revalidateAgreement(waybillNos);
        return new CommonResponse<>("Revalidated successfully", 200);
    }

    @PutMapping("/revalidate-new")
    public CommonResponse<String> revalidateNewAgreement(@Valid @RequestBody List<String> waybillNos) {
        /*invoiceService.revalidateAgreement(waybillNos);*/
        return new CommonResponse<>("Revalidated on new agreement successfully", 200);
    }

}
