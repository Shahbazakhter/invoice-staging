package com.ajex.invoice.staging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AimsInvoiceData {

    private String mainAccount;
    private String subAccountNo;
    private String waybillNo;
    private String serviceType;
    private String subServiceType;
    private String temperature;
    private Double volumeOfCargo;
    private Double originOdaCharge;
    private Double destinationOdaCharge;
    private String referenceNo;
    private Instant pickupDate;
    private Instant deliveryDate;
    private String origin;
    private String destination;
    private Integer pieces;
    private Double weight;
    private String currency;
    private String paymentTerms; //... e.g. 30 Net
    private String taxCode; //... VAT-0%

    private Double serviceCharge;
    private Double podCharge;
    private Double labourCharge;
    private Double demurrageCharge;
    private Double codServiceCharge;
    private Double stdShipmentAmount;
    private Double forkliftAmount;
    private Double additionalCharge;
    private Double fsc;
    private Double netAmount;
    private Double vatPercentage;
    private Double vatAmount;
    private Double totalAmount;
    private BigDecimal unitSellingPrice;
}
