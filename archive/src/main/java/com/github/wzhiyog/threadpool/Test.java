package com.github.wzhiyog.threadpool;

public class Test {
    public static void main(String[] args) {
        // 1. 基础使用
        GradientExecutor executor = GradientExecutor.getInstance();
        executor.execute("image-processing", () -> {
            System.out.println("processImage(image)");
            ;
        }, GradientExecutor.Opt.withMaxConcurrent(100));
    }
}
