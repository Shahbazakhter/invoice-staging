package com.ajex.invoice.staging.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomError {

    INTERNAL_SERVER_ERROR("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    EMPLOYEE_NOT_FOUND("Employee not found", HttpStatus.BAD_REQUEST),
    GEO_FENCING_ID_FOUND("Geo Fencing Id not found", HttpStatus.BAD_REQUEST),
    CITY_ID_FOUND("City not found", HttpStatus.BAD_REQUEST),
    SHIPPER_ID_NOT_FOUND("Shipper Id not found", HttpStatus.BAD_REQUEST),
    GEO_FENCING_ALREADY_EXISTS("Geo Fencing already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found", HttpStatus.BAD_REQUEST),
    CONSIGNEE_ID_NOT_FOUND("Consignee Id not found", HttpStatus.BAD_REQUEST),
    STATION_ID_FOUND("Station Id not found", HttpStatus.BAD_REQUEST),
    DATABASE_EXCEPTION("Database error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final int statusCode;

    CustomError(String message, HttpStatus status) {
        this.message = message;
        this.statusCode = status.value();
    }
}