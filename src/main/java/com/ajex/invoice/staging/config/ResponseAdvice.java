package com.ajex.invoice.staging.config;

import com.ajex.invoice.staging.dto.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final static String GET_SUCCESS = "Retrieved Successfully!";
    private final static String POST_SUCCESS = "Created Successfully!";
    private final static String PUT_SUCCESS = "Updated Successfully!";
    private final static String DELETE_SUCCESS = "Deleted Successfully!";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public CommonResponse beforeBodyWrite(Object body, MethodParameter returnType,
                                          MediaType selectedContentType,
                                          Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                          ServerHttpRequest request,
                                          ServerHttpResponse response) {

        if (body instanceof CommonResponse) {
            CommonResponse commonResponse = (CommonResponse) body;
            response.setStatusCode(HttpStatus.valueOf(commonResponse.getStatusCode()));
            return commonResponse;
        } else if (body instanceof Exception) {
            throw new RuntimeException();
        } else if (body instanceof LinkedHashMap) {
            Map<String, Object> exception = (LinkedHashMap) body;
            if (exception.get("error") == null) {
                return new CommonResponse<>(HttpStatus.OK, body);
            }
            return new CommonResponse<>((String) exception.get("error"), (Integer) exception.get("status"));
        } else {
            CommonResponse commonResponse = null;
            if (request.getMethod().equals(HttpMethod.GET)) {
                commonResponse = new CommonResponse<>(GET_SUCCESS, HttpStatus.OK.value(), body);
            } else if (request.getMethod().equals(HttpMethod.PUT)) {
                commonResponse = new CommonResponse<>(PUT_SUCCESS, HttpStatus.OK.value(), body);
            } else if (request.getMethod().equals(HttpMethod.POST)) {
                commonResponse = new CommonResponse<>(POST_SUCCESS, HttpStatus.OK.value(), body);
            } else if (request.getMethod().equals(HttpMethod.DELETE)) {
                commonResponse = new CommonResponse<>(DELETE_SUCCESS, HttpStatus.OK.value(), body);
            } else {
                commonResponse = new CommonResponse<>(HttpStatus.OK, body);
            }
            return commonResponse;
        }
    }
}