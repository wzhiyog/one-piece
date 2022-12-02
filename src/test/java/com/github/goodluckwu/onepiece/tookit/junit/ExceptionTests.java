package com.github.goodluckwu.onepiece.tookit.junit;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ExceptionTests {
    @Test
    public void test(){
        Exception exception = assertThrows(ArithmeticException.class, () -> System.out.println(1 / 0));
        assertThat(exception).hasNoCause().hasMessage("/ by zero").isInstanceOf(ArithmeticException.class);
    }
}
