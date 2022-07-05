package com.github.goodluckwu.onepiece;

import org.hashids.Hashids;

import java.util.UUID;

public class HashidsTests {
    public static void main(String[] args) {
        Hashids hashids = new Hashids("this is my salt", 4);
        System.out.println(hashids.encodeHex("abc"));
        System.out.println(hashids.encode(12345L));
        System.out.println(hashids.encode(1L));
        System.out.println(hashids.encodeHex("a"));
        System.out.println(hashids.encodeHex(UUID.randomUUID().toString()));
    }
}
