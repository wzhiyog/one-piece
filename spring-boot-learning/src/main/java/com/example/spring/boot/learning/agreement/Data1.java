package com.example.spring.boot.learning.agreement;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Data1 {
    private String data;
    private Integer integer = 1;
    private BigDecimal bigDecimal = new BigDecimal("1.025");
    private LocalDateTime localDateTime = LocalDateTime.now();
    private LocalDate localDate = LocalDate.now();
    private Boolean bool = true;
    private Date date = new Date();
    private String numberString = "1.025";
}
