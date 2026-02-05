package com.ajex.invoice.staging.service;

import com.ajex.invoice.staging.dto.LoginRequest;
import com.ajex.invoice.staging.dto.LoginResponse;
import com.ajex.invoice.staging.integration.AuthFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthFeignClient authFeignClient;

    private String token;
    private LocalDateTime expiryTime;

    private static final String USERNAME = "123";
    private static final String PASSWORD = "Ajex@123";

    public synchronized String getToken() {

        // If token exists and still valid → reuse
        if (token != null && expiryTime != null &&
            LocalDateTime.now().isBefore(expiryTime)) {
            return token;
        }

        // Otherwise call login API
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        LoginResponse response = authFeignClient.login(request);

        this.token = response.getAccessToken();

        // Set expiry (subtract 1 min safety buffer)
        this.expiryTime = LocalDateTime.now()
                .plusSeconds(response.getExpiresIn() - 600);

        return token;
    }
}
