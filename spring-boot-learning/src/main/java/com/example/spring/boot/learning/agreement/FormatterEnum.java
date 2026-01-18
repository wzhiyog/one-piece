package com.example.spring.boot.learning.agreement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormatterEnum {
    DATETIME(1, "日期时间"),
    // https://blog.csdn.net/tc979907461/article/details/106558491
    NUMBER(2, "数字"),
    ;

    private final int code;
    private final String desc;

    public static FormatterEnum getByCode(int code) {
        for (FormatterEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
