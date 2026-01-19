package com.ajex.invoice.staging.constant;

public enum InvoiceDetailStatus {

    INVOICE_STAGE("invoice_stage"),
    INVOICE_STAGE_PENDING("invoice_stage_pending"),
    INVOICE_STAGE_DONE("invoice_stage_done");

    private final String value;

    InvoiceDetailStatus(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }

}
