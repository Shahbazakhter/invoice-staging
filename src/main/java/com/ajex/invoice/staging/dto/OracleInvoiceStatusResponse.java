package com.ajex.invoice.staging.dto;

import lombok.Data;

import java.util.List;

@Data
public class OracleInvoiceStatusResponse {
    
    private List<InvoiceStatus> successfulInvoices;
    private List<InvoiceStatus> failedInvoices;


}