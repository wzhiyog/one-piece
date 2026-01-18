package com.example.spring.boot.learning.agreement;

import org.springframework.stereotype.Component;

@Component
public class Data2Service implements DataLoader<Data2> {
    @Override
    public DataLoaderEnum getDataLoader() {
        return DataLoaderEnum.DATA2;
    }

    @Override
    public Data2 loadData(ContractReq context) {
        Data2 data1 = new Data2();
        data1.setData("data2");
        return data1;
    }
}
