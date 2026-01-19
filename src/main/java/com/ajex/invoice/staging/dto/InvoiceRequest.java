package com.ajex.invoice.staging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceRequest {

    private Long stagingId;
    private String waybillNo;
    private String actionType;

}
