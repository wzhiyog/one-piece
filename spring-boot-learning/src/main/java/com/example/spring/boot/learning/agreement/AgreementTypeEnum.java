package com.example.spring.boot.learning.agreement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AgreementTypeEnum {
    // 贷款协议
    LOAN_AGREEMENT("LOAN_AGREEMENT", "贷款协议"),
    ;
    private final String agreementType;
    private final String agreementName;
}
