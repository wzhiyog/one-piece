package com.example.spring.boot.learning.agreement;

import org.springframework.stereotype.Component;

@Component
public class Data1Service implements DataLoader<Data1> {
    @Override
    public Data1 loadData(ContractReq context) {
        Data1 data1 = new Data1();
        data1.setData("data1");
        return data1;
    }

    @Override
    public DataLoaderEnum getDataLoader() {
        return DataLoaderEnum.DATA1;
    }
}
