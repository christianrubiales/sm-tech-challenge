package com.christianrubiales.smchallenge.api;

import jakarta.servlet.http.HttpServletRequest;

public class ControllerUtils {

    public static String getUserIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
