package com.github.goodluckwu.onepiece.validate;

import java.util.Objects;
import java.util.function.Predicate;

public class Validator <T> {

    /**
     * 初始化为 true  true &&其它布尔值时由其它布尔值决定真假
     */
    private Predicate<T> predicate;

    /**
     * 添加一个校验策略，可以无限续杯?
     *
     * @param predicate the predicate
     * @return the validator
     */
    public Validator<T> and(Predicate<T> predicate) {
        if(Objects.isNull(this.predicate)){
            this.predicate = t -> true;
        }
        this.predicate = this.predicate.and(predicate);
        return this;
    }

    public Validator<T> or(Predicate<T> predicate) {
        if(Objects.isNull(this.predicate)){
            this.predicate = t -> false;
        }
        this.predicate = this.predicate.or(predicate);
        return this;
    }

    /**
     * 执行校验
     *
     * @param t the t
     * @return the boolean
     */
    public boolean validate(T t) {
        return predicate.test(t);
    }

    public static void main(String[] args) {
        Validator<String> validator = new Validator<>();
        System.out.println(validator.or(s -> s.startsWith("A"))
                .and(s -> s.startsWith("a"))
            .validate("a"));
    }
}
