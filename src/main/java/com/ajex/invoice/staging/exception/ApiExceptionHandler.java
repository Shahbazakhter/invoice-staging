package com.ajex.invoice.staging.exception;

import com.ajex.invoice.staging.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(InvoiceStagingException.class)
    @ResponseBody
    public CommonResponse handleCustomException(InvoiceStagingException e) {
        log.error("Error Occurred", e);
        return new CommonResponse(e.getErrorMessage(), e.getStatusCode(), null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResponse handleException(Exception e) {
        log.error("Error Occurred", e);
        return new CommonResponse(CustomError.INTERNAL_SERVER_ERROR.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
