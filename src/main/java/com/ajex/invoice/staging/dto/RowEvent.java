package com.ajex.invoice.staging.dto;

import java.time.Instant;

public record RowEvent(
        String aggregationKey,   // e.g. REPORT_TYPE or USER_ID
        String waybillNo,
        Instant createdAt
) {}
