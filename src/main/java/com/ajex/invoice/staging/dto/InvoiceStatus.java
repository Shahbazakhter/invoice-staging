package com.ajex.invoice.staging.dto;

import lombok.Data;

@Data
public class InvoiceStatus {
        private String invoiceId;
        private String waybillNumber;
        private String transactionId;
        private Long transactionDate;
        private String responseCode;
        private String responseMessage;
}