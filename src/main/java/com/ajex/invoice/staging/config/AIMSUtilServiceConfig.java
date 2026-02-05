package com.ajex.invoice.staging.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AIMSUtilServiceConfig {

    /**
     * Add Authorization header for every request
     */
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
////            requestTemplate.header("Authorization", "Bearer " + splServiceBearerToken);
////            requestTemplate.header("Accept", "application/json");
//            requestTemplate.header("username", "123");
//            requestTemplate.header("password", "Ajex@123");
////            requestTemplate.header("roles", "[admin]");
//        };
//    }

}
