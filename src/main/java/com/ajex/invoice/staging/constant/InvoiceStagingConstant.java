package com.ajex.invoice.staging.constant;

import java.time.Instant;

public interface InvoiceStagingConstant {

    String LOG_UPDATE_STATUS = "Updated to {}";

    static Instant getUTCInstant() {
        return Instant.now().atOffset(java.time.ZoneOffset.UTC).toInstant();
    }
}
