package com.github.wzhiyog.exception;

public enum SystemErrorCodes implements Error {
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误代码：%s");

    private final String code;
    private final String description;

    SystemErrorCodes(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
