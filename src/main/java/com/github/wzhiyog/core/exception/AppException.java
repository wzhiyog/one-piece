package com.github.wzhiyog.core.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.wzhiyog.core.EnumValue;

public class AppException extends RuntimeException {
    private Error error;
    private final Map<String, Object> data = new HashMap<>();
    private boolean logData;

    public AppException() {
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public Error getError() {
        return error;
    }

    public AppException setError(Error error) {
        this.error = error;
        return this;
    }

    public AppException enableLogData() {
        this.logData = true;
        return this;
    }

    public AppException put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) this.data.get(key);
    }

    private String formatMessage(EnumValue enumValue, String message) {
        if (message == null || message.trim().isEmpty()) {
            return String.format("[(%s)%s]", enumValue.getCode(), enumValue.getDescription());
        }
        return String.format("[(%s)%s]%s", enumValue.getCode(), enumValue.getDescription(), message);
    }

    private Error getUnknownError() {
        String errorCode = Integer.toString(UUID.randomUUID().hashCode(), Character.MAX_RADIX).toUpperCase()
                .replace(' ', '0')
                .replace('O', '0')
                .replace('I', '1')
                .replace('-', ' ')
                .trim();
        return new Error() {
            @Override
            public String getCode() {
                return "unknown_error";
            }

            @Override
            public String getDescription() {
                return String.format("unknown error: %s", errorCode);
            }
        };
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (this.error == null) {
            this.error = getUnknownError();
        }
        message = this.formatMessage(this.error, message);
        if (logData && !data.isEmpty()) {
            return String.format("message: %s, data: %s", message, data);
        }
        return message;
    }

    public static void main(String[] args) {
        throw new AppException("123", new AppException(new RuntimeException())).put("a", "a").enableLogData();
    }
}
