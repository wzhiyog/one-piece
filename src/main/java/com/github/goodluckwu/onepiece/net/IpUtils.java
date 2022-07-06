package com.github.goodluckwu.onepiece.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpUtils {
    private static final Logger log = LoggerFactory.getLogger(IpUtils.class);

    private IpUtils(){}

    public static InetAddress getLocalHostExactAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (inetAddr.isLoopbackAddress()) {
                        continue;
                    }
                    if (iface.isUp() && inetAddr.isSiteLocalAddress()) {
                        // 如果是site-local地址，就是它了 就是我们要找的
                        // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                        return inetAddr;
                    }

                    // 若不是site-local地址 那就记录下该地址当作候选
                    if (candidateAddress == null) {
                        candidateAddress = inetAddr;
                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return InetAddress.getLocalHost();
    }
}

