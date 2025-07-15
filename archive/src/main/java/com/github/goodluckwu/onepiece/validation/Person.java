package com.github.goodluckwu.onepiece.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Person {
    @NotNull
    private String name;
    @NotNull
    @Positive
    private Integer age;
    @Valid
    @NotNull
    private InnerChild child;
    public static class InnerChild{
        @NotNull
        private String name;
        @NotNull
        @Positive
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "InnerChild{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public InnerChild getChild() {
        return child;
    }

    public void setChild(InnerChild child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", child=" + child +
                '}';
    }
}
