package com.example.spring.boot.learning.agreement;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FillContext {
    private AgreementTypeEnum agreementType;

    private List<FillItem> fillItemList;

    private Map<String, Object> dataMap = new HashMap<>();

    private Map<String, String> agreementParamMap = new HashMap<>();

    public void addAgreementParam(String paramName, String paramValue) {
        agreementParamMap.put(paramName, paramValue);
    }
}
