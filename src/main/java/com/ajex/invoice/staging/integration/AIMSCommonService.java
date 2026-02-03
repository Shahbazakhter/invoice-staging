package com.ajex.invoice.staging.integration;

import com.ajex.invoice.staging.config.AIMSUtilServiceConfig;
import com.ajex.invoice.staging.dto.OracleInvoiceStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(url = "${ajex.tms-common-service.url:http://localhost:6203}",
        name = "AIMSInvoiceService",
        configuration = AIMSUtilServiceConfig.class)
public interface AIMSCommonService {

    @PostMapping("/api/v1/oracle-invoice")
    void updateOracleInvoiceResponse(@RequestBody OracleInvoiceStatusResponse oracleInvoiceStatusResponse);

    @PostMapping("/api/v1/manifest/manual-recalculate")
    void recalculateAgreement(@RequestBody List<String> waybillNo);

    @PutMapping("/api/v1/manifest/revoke-manifester-confirmation")
    void revokeManifesterConfirmation(@RequestBody List<String> waybillNo);


}