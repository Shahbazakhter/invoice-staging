package com.ajex.invoice.staging.controller;

import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.CommonResponse;
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

    @GetMapping
    public List<LandFreightInvoiceDetail> getAllInvoiceStages(
            @RequestParam(value = "status") String status,
            @RequestParam(defaultValue = "land_freight", required = false) String businessLine) {
        return invoiceService.getAllInvoices(status, businessLine);
    }

    @PostMapping("/push")
    public CommonResponse<String> stagingForPush(@Valid @RequestBody List<String> waybillNos) {
        invoiceService.stageForPush(waybillNos);
        return new CommonResponse<>("SUCCESS", 200);
    }

}
