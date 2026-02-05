package com.ajex.invoice.staging.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceFilterRequest {

    private List<String> businessUnits;
    private List<String> customerSubAccounts;
    private List<String> waybillNos;
    private List<String> statuses;
    private String businessLine;

}
