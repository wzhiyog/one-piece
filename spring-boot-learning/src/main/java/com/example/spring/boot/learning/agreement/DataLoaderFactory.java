package com.example.spring.boot.learning.agreement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.EnumMap;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class DataLoaderFactory implements InitializingBean {
    private final List<DataLoader<?>> dataLoaders;

    private final EnumMap<DataLoaderEnum, DataLoader<?>> dataLoaderMap = new EnumMap<>(DataLoaderEnum.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(dataLoaders)) {
            return;
        }

        for (DataLoader<?> dataLoader : dataLoaders) {
            dataLoaderMap.put(dataLoader.getDataLoader(), dataLoader);
        }

        log.info("DataLoaderFactory 初始化完成，数量: {}", dataLoaderMap.size());
    }

    public DataLoader<?> getDataLoader(DataLoaderEnum dataLoaderEnum) {
        return dataLoaderMap.get(dataLoaderEnum);
    }
}
