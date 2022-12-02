package com.github.goodluckwu.onepiece.tookit.junit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterizedTests {
    @ParameterizedTest
    @MethodSource
    void testCapitalize(String input, String result) {
        assertEquals(result, StringUtils.capitalize(input));
    }

    static List<Arguments> testCapitalize() {
        return List.of( // arguments:
                Arguments.of("abc", "Abc"), //
                Arguments.of("APPLE", "APPLE"), //
                Arguments.of("gooD", "GooD"));
    }

    @ParameterizedTest
    @CsvSource({ "abc, Abc", "APPLE, APPLE", "gooD, GooD" })
    void testCapitalize2(String input, String result) {
        assertEquals(result, StringUtils.capitalize(input));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, -5, -100 })
    void testAbsNegative(int x) {
        assertEquals(-x, Math.abs(x));
    }
}
