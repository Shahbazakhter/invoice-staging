package com.ajex.invoice.staging.constant;

public enum InvoiceDetailStatus {

    INVOICE_STAGE("invoice_stage"),
    INVOICE_STAGE_UPDATING("invoice_stage_updating"),
    INVOICE_STAGE_PENDING("invoice_stage_pending"),
    INVOICE_STAGE_DONE("invoice_stage_done"),
    PUSHED_TO_ORACLE("pushed_to_oracle"),
    ERROR("error");

    private final String value;

    InvoiceDetailStatus(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }

}
