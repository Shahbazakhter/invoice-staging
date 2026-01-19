package com.ajex.invoice.staging.constant;

import com.ajex.invoice.staging.document.InvoiceDetail;
import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BusinessLine {


    LAND_FREIGHT("land_freight", LandFreightInvoiceDetail.class);

    private final String value;
    private Class<? extends InvoiceDetail> implementationClass;

    BusinessLine(final String newValue, Class<? extends InvoiceDetail> clazz) {
        value = newValue;
        this.implementationClass = clazz;
    }

    public String getValue() {
        return value;
    }

    public Class<? extends InvoiceDetail> getImplementationClass() {
        return implementationClass;
    }

    private static Map<String, BusinessLine> businessLineByValue;

    public static Optional<BusinessLine> getBusinessLineByValue(String value) {

        if (CollectionUtils.isEmpty(businessLineByValue)) {
            businessLineByValue = Stream.of(BusinessLine.values())
                    .collect(Collectors.toMap(e -> e.getValue(), Function.identity()));
        }

        return Optional.ofNullable(businessLineByValue.get(value));
    }
}
