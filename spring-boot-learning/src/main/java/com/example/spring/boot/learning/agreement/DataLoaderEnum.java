package com.example.spring.boot.learning.agreement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataLoaderEnum {
    FORM(0, "表单数据源"),
    DATA2(2, "data2"),
    DATA1(3, "data1"),
    ;

    private final int code;
    private final String desc;

    public static DataLoaderEnum getByCode(int code) {
        for (DataLoaderEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
