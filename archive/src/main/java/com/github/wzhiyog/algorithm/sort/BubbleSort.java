package com.github.wzhiyog.algorithm.sort;

import java.util.stream.IntStream;

public class BubbleSort {
    public static void main(String[] args) {
        IntStream.range(0, 10).forEach(i -> {
            System.out.println((i % 2) + " == " + (i % 4));
        });
    }
}
