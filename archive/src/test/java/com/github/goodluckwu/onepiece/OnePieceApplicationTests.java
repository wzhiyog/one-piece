package com.github.goodluckwu.onepiece;

import com.github.wzhiyog.exception.Error;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OnePieceApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(OnePieceApplicationTests.class);

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/hello1?name=tracer");
    }

    @Test
    void contextLoads() {
        log.info("{}", restTemplate.getForEntity(base.toString(), String.class));
    }

    public static void main(String[] args) {
        try (ScanResult scanResult = new ClassGraph().verbose().enableAllInfo().acceptPackages("com.github.wzhiyog").scan()) {
            ClassInfoList classesImplementing = scanResult.getClassesImplementing(Error.class);
            classesImplementing.forEach(System.out::println);
        }
    }

}
