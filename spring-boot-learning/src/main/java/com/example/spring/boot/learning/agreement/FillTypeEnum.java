package com.example.spring.boot.learning.agreement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FillTypeEnum {
    FIXED(1, "固定值"),
    EXPRESSION(2, "表达式"),
    EMPTY(3, "空字符串"),
    ;

    private final int code;
    private final String desc;

    public static FillTypeEnum getByCode(int code) {
        for (FillTypeEnum value : FillTypeEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
