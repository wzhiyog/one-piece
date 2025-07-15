package com.github.wzhiyog.exception;

import com.github.wzhiyog.core.EnumValue;

public interface Error extends EnumValue {
    default Error format(Object... args) {
        return new Error() {
            @Override
            public String getCode() {
                return Error.this.getCode();
            }

            @Override
            public String getDescription() {
                return String.format(Error.this.getDescription(), args);
            }
        };
    }
}
