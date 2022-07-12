package com.github.goodluckwu.onepiece.trace;

import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class TracerUtils {
    public static String P_ID_CACHE = null;

    public static String getPID() {
        //check pid is cached
        if (P_ID_CACHE != null) {
            return P_ID_CACHE;
        }
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();

        if (!StringUtils.hasText(processName)) {
            return "";
        }

        String[] processSplitName = processName.split("@");

        if (processSplitName.length == 0) {
            return "";
        }

        String pid = processSplitName[0];

        if (!StringUtils.hasText(pid)) {
            return "";
        }
        P_ID_CACHE = pid;
        return pid;
    }

    public static String getInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.getHostAddress().contains(":")) {
                        return address.getHostAddress();
                    }
                }
            }
            return null;
        } catch (Throwable t) {
            return null;
        }
    }
}
