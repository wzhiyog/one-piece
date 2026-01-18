package com.example.spring.boot.learning.agreement;

/**
 * 填充数据加载器
 *
 * @param <T> 返回值类型，建议使用强类型
 */
public interface DataLoader<T> {
    /**
     * 加载数据
     */
    T loadData(ContractReq context);

    DataLoaderEnum getDataLoader();
}
