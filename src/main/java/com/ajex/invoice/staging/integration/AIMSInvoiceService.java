package com.ajex.invoice.staging.integration;

import com.ajex.invoice.staging.config.AIMSUtilServiceConfig;
import com.ajex.invoice.staging.dto.OracleInvoiceStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(url = "${ajex.tms-common-service.url:http://localhost:6203}",
        name = "AIMSInvoiceService",
        configuration = AIMSUtilServiceConfig.class)
public interface AIMSInvoiceService {

    @PostMapping("/api/v1/oracle-invoice")
    void updateOracleInvoiceResponse(OracleInvoiceStatusResponse oracleInvoiceStatusResponse);

}