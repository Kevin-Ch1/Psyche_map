package com.psychemap.audit.service;

import java.net.InetAddress;

public final class NetworkUtil {

    private NetworkUtil() {
    }

    public static String getLocalIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            if (hostAddress != null && !hostAddress.isBlank()) {
                return hostAddress;
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }
}
