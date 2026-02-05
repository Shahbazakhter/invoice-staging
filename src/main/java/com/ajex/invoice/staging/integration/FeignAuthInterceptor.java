package com.ajex.invoice.staging.integration;

import com.ajex.invoice.staging.service.TokenService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignAuthInterceptor {

    private final TokenService tokenService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            // Skip login API call itself
            if (requestTemplate.url().contains("/auth/login")) {
                return;
            }

            String token = tokenService.getToken();

            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}
