package com.ajex.invoice.staging.integration;

import com.ajex.invoice.staging.dto.LoginRequest;
import com.ajex.invoice.staging.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "authClient",
        url = "${auth.service.url:https://apps-sit.aj-ex.com/authentication-service}"
)
public interface AuthFeignClient {

    @PostMapping("/api/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);
}