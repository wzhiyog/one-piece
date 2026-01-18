package com.example.spring.boot.learning;

import com.example.spring.boot.learning.agreement.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootTest
class SpringBootLearningApplicationTests {
    @Autowired
    private FillService fillService;

    @Test
    void contextLoads() throws IOException {
        ContractReq contractReq = new ContractReq();
        FillItem fillItem = new FillItem();
        fillItem.setItemName("NameB");
        fillItem.setExpression("date");
        fillItem.setFillType(FillTypeEnum.EXPRESSION.getCode());
        fillItem.setDataLoader(DataLoaderEnum.DATA1.getCode());
        fillItem.setOrder(1);
        fillItem.setFormatter(FormatterEnum.DATETIME.getCode());
        fillItem.setFormatPattern("yyyy-MM-dd HHmmss");

        FillItem fillItem2 = new FillItem();
        fillItem2.setItemName("NameA");
        fillItem2.setExpression("numberString");// map要用中括号
        fillItem2.setFillType(FillTypeEnum.EXPRESSION.getCode());
        fillItem2.setDataLoader(DataLoaderEnum.DATA1.getCode());
        fillItem2.setOrder(2);
        fillItem2.setFormatter(FormatterEnum.NUMBER.getCode());
        fillItem2.setFormatPattern("¤#.##");
        contractReq.setFillItemList(Arrays.asList(fillItem, fillItem2));

        byte[] bytes = fillService.fillPdf(contractReq);
        Files.write(Paths.get("C:\\Users\\wuzhi\\Desktop\\loan-agreement-template-zh-fill.pdf"), bytes);
    }

}