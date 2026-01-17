package com.example.spring.boot.learning.agreement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgreementParam {
    private String paramName;
    private String paramValue;
}
