//package com.ajex.invoice.staging.util;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Objects;
//
//public class AuthUtil {
//
//    private static final ThreadLocal<String> USER_NAME = new ThreadLocal<>();
//
//    public static String getUserName() {
//        if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
//            return USER_NAME.get();
//        }
//        return SecurityContextHolder.getContext().getAuthentication().getName();
//    }
//
//    public static void removeUserName() {
//        USER_NAME.remove();
//    }
//
//}
