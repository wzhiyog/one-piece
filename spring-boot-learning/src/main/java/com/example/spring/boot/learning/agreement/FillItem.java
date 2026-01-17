package com.example.spring.boot.learning.agreement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FillItem {
    // 字段名
    private String itemName;
    // 字段表达式
    private String expression;
    // 填充类型
    private FillTypeEnum fillType;
    // 数据加载器
    private DataLoaderEnum dataLoader;
    // 排序
    private int order;
    // 格式化器
    private FormatterEnum formatter;
    // 格式化模板
    private String formatPattern;
}