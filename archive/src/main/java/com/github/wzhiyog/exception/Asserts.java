package com.github.wzhiyog.exception;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 断言工具类
 */
public abstract class Asserts {

    public static boolean isNull(Object object) {
        return Objects.isNull(object);
    }

    public static void CheckNull(Object object, Supplier<? extends ServiceException> exceptionSupplier) {
        if (nonNull(object)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean nonNull(Object object) {
        return Objects.nonNull(object);
    }

    public static void checkNonNull(Object object, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isNull(object)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isTrue(boolean condition) {
        return condition;
    }

    public static void checkTrue(boolean condition, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isFalse(condition)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isFalse(boolean condition) {
        return !condition;
    }

    public static void checkFalse(boolean condition, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isTrue(condition)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    public static void isEmpty(Collection<?> collection, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isNotEmpty(collection)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return isFalse(CollectionUtils.isEmpty(collection));
    }

    public static void checkNotEmpty(Collection<?> collection, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isEmpty(collection)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return CollectionUtils.isEmpty(map);
    }

    public static void checkEmpty(Map<?, ?> map, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isNotEmpty(map)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return isFalse(CollectionUtils.isEmpty(map));
    }

    public static void checkNotEmpty(Map<?, ?> map, Supplier<? extends ServiceException> supplierException) {
        if (isEmpty(map)) {
            throw supplierException.get();
        }
    }

    public static boolean isEmpty(String string) {
        return isFalse(StringUtils.hasLength(string));
    }

    public static void checkEmpty(String string, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isNotEmpty(string)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isNotEmpty(String string) {
        return StringUtils.hasLength(string);
    }

    public static void checkNotEmpty(String string, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isEmpty(string)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isBlank(String string) {
        return isFalse(StringUtils.hasText(string));
    }

    public static void checkBlank(String string, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isNotBlank(string)) {
            throw exceptionSupplier.get();
        }
    }

    public static boolean isNotBlank(String string) {
        return StringUtils.hasText(string);
    }

    public static void checkNotBlank(String string, Supplier<? extends ServiceException> exceptionSupplier) {
        if (isBlank(string)) {
            throw exceptionSupplier.get();
        }
    }
}
