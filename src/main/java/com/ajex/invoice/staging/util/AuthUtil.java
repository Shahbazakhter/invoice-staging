package com.ajex.tmscommonservice.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

public class AuthUtil {

    private static ThreadLocal<String> userName = new ThreadLocal<>();

    public static String getUserName(){
        if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            return userName.get();
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static void setUserName(String user) {
        if (StringUtils.isNotEmpty(user)) {
            userName.set(user);
        } else {
            userName.set("123");
        }
    }

    public static void removeUserName() {
        userName.remove();
    }

    public static List<String> getUserRoles() {
        return null;
    }
}