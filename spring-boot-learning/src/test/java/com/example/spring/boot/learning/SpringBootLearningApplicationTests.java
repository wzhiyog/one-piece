package com.example.spring.boot.learning;

import com.example.spring.boot.learning.agreement.ContractReq;
import com.example.spring.boot.learning.agreement.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class SpringBootLearningApplicationTests {
    @Autowired
    private ContractService contractService;

    @Test
    void contextLoads() throws IOException {

        byte[] bytes = contractService.createContract(new ContractReq());
        Files.write(Paths.get("C:\\Users\\wuzhi\\Desktop\\loan-agreement-template-zh-fill.pdf"), bytes);
    }

}