package com.github.goodluckwu.onepiece.sequence;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

public class RequestId {
    // 自增id，用于requestId的生成过程
    private static final AtomicLong lastId = new AtomicLong();

    // 启动加载时的时间戳，用于requestId的生成过程
    private static final long startTimeStamp = System.currentTimeMillis();

    // 本机ip地址，用于requestId的生成过程
    private static final InetAddress ip = getLocalHostExactAddress();

    public static void main(String[] args) {
        System.out.println(resolveReqId());
    }

    private static String resolveReqId() {
        // 规则： hexIp(ip)base36(timestamp)-seq
        return hexIp(ip) + Long.toString(startTimeStamp, Character.MAX_RADIX) + "-" + lastId.incrementAndGet();
    }

    // 将ip转换为定长8个字符的16进制表示形式：255.255.255.255 -> FFFFFFFF
    private static String hexIp(InetAddress ip) {
        if(ip == null){
            return ShortUuid.getShortUuid();
        }else{
            String ipStr = ip.getHostAddress();
            StringBuilder sb = new StringBuilder();
            for (String seg : ipStr.split("\\.")) {
                String h = Integer.toHexString(Integer.parseInt(seg));
                if (h.length() == 1) {
                    sb.append("0");
                }
                sb.append(h);
            }
            return sb.toString();
        }
    }


    public static InetAddress getLocalHostExactAddress() {
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
            e.printStackTrace();
        }
        return null;
    }
}

