package com.ajex.invoice.staging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T>{
    private String message;
    private int statusCode;
    private T data;
    public static final String RETRIEVED_SUCCESSFULLY = "Retrieved Successfully!";
    private Integer page;
    private Integer numberOfElements;
    private Integer totalPages;
    private Boolean isLast;
    private Long totalNumberOfAllElements;

    private Object response;

    public CommonResponse(String message, int statusCode, T data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }
    public CommonResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }

    public CommonResponse(HttpStatus status, T data) {
        this.data = data;
        this.statusCode = status.value();
        this.message = status.getReasonPhrase();
    }
    public CommonResponse(HttpStatus status) {
        this.statusCode = status.value();
        this.message = status.getReasonPhrase();
    }

    public CommonResponse(String message, HttpStatus status) {
        this.statusCode = status.value();
        this.message = message;
    }

    public CommonResponse(Page<?> page, T data) {
        this.message = RETRIEVED_SUCCESSFULLY;
        this.statusCode = HttpStatus.OK.value();
        this.page = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
        this.data = data;
        this.totalNumberOfAllElements = page.getTotalElements();
    }

    public CommonResponse(Page<?> page, T data, boolean response) {
        this.message = RETRIEVED_SUCCESSFULLY;
        this.statusCode = HttpStatus.OK.value();
        this.page = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
        this.response = data;
        this.totalNumberOfAllElements = page.getTotalElements();
    }

    public CommonResponse() {
    }
}
