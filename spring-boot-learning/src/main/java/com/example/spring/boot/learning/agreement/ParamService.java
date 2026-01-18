package com.example.spring.boot.learning.agreement;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ParamService implements DataLoader<Map<String, String>> {
    @Override
    public Map<String, String> loadData(ContractReq context) {
        return new HashMap<>();
    }

    @Override
    public DataLoaderEnum getDataLoader() {
        return DataLoaderEnum.FORM;
    }
}
