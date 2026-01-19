package com.ajex.invoice.staging.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

@Getter
@Setter
public class InvoiceStagingException extends RuntimeException {
    private String errorMessage;
    private Integer statusCode;

    public InvoiceStagingException(String errorMessage, Integer statusCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public InvoiceStagingException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.errorMessage = status.getReasonPhrase();
        this.statusCode = status.value();
    }

    public InvoiceStagingException(CustomError error) {
        super(error.getMessage());
        this.errorMessage = error.getMessage();
        this.statusCode = error.getStatusCode();
    }

    public InvoiceStagingException(CustomError error, String message) {
        super(error.getMessage() + " " + (ObjectUtils.isEmpty(message) ? "" : message));
        this.errorMessage = error.getMessage() + " " + (ObjectUtils.isEmpty(message) ? "" : message);
        this.statusCode = error.getStatusCode();
    }

    public InvoiceStagingException(CustomError error, Integer row) {
        super((row == null) ? error.getMessage() : error.getMessage() + " At Row " + row);
        this.errorMessage = (row == null) ? error.getMessage() : error.getMessage() + " At Row " + row;
        this.statusCode = error.getStatusCode();
    }

    public InvoiceStagingException(String errorMessage, Integer statusCode, Integer row) {
        super((row == null) ? errorMessage : errorMessage + " At Row " + row);
        this.errorMessage = (row == null) ? errorMessage : errorMessage + " At Row " + row;
        this.statusCode = statusCode;
    }
}

