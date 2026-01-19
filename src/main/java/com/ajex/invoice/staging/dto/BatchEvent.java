package com.ajex.invoice.staging.dto;

import java.util.List;

public record BatchEvent(
        String batchId,
        String aggregationKey,
        List<String> waybillNos,
        String triggerType  // SIZE | TIME
) {
}
