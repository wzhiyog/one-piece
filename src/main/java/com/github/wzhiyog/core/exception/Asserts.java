package com.github.wzhiyog.core.exception;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 断言工具类
 */
public abstract class Asserts {
    @FunctionalInterface
    public interface ThrowException {
        void thenThrow(Supplier<? extends ServiceException> supplier);
    }

    public static ThrowException isNull(Object object) {
        return supplier -> {
            if (Objects.nonNull(object)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException nonNull(Object object) {
        return supplier -> {
            if (Objects.isNull(object)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isTrue(boolean condition) {
        return supplier -> {
            if (!condition) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isFalse(boolean condition) {
        return supplier -> {
            if (condition) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isEmpty(Collection<?> collection) {
        return supplier -> {
            if (!CollectionUtils.isEmpty(collection)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isNotEmpty(Collection<?> collection) {
        return supplier -> {
            if (CollectionUtils.isEmpty(collection)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isEmpty(Map<?, ?> map) {
        return supplier -> {
            if (!CollectionUtils.isEmpty(map)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isNotEmpty(Map<?, ?> map) {
        return supplier -> {
            if (CollectionUtils.isEmpty(map)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isEmpty(String string) {
        return supplier -> {
            if (StringUtils.hasLength(string)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isNotEmpty(String string) {
        return supplier -> {
            if (!StringUtils.hasLength(string)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isBlank(String string) {
        return supplier -> {
            if (StringUtils.hasText(string)) {
                throw supplier.get();
            }
        };
    }

    public static ThrowException isNotBlank(String string) {
        return supplier -> {
            if (!StringUtils.hasText(string)) {
                throw supplier.get();
            }
        };
    }
}
