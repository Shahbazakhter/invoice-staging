package com.ajex.invoice.staging.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private long expiresIn; // seconds (if available)
}