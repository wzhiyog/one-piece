package com.github.goodluckwu.onepiece.test;

import com.github.goodluckwu.onepiece.trace.MDCTraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello1")
    public String hello(String name){
        log.info("hello1");
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(MDCTraceUtils.TRACE_ID, MDCTraceUtils.getTraceId());
        headers.set(MDCTraceUtils.SPAN_ID, MDCTraceUtils.generateNextSpanId());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8080/hello2?name=hello2", HttpMethod.GET, httpEntity, String.class);
        log.info("hello1");
        headers.set(MDCTraceUtils.SPAN_ID, MDCTraceUtils.generateNextSpanId());
        ResponseEntity<String> exchange2 = restTemplate.exchange("http://localhost:8080/hello2?name=hello3", HttpMethod.GET, httpEntity, String.class);
        log.info("hello1");
        return "hello, " + name + "!";
    }

    @GetMapping("/hello2")
    public String hello2(String name){
        log.info("hello2");
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(MDCTraceUtils.TRACE_ID, MDCTraceUtils.getTraceId());
        headers.set(MDCTraceUtils.SPAN_ID, MDCTraceUtils.generateNextSpanId());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8080/hello3?name=hello3", HttpMethod.GET, httpEntity, String.class);
        log.info("hello2");
        return "hello2, " + name + "!";
    }

    @GetMapping("/hello3")
    public String hello3(String name){
        log.info("hello3");
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(MDCTraceUtils.TRACE_ID, MDCTraceUtils.getTraceId());
        headers.set(MDCTraceUtils.SPAN_ID, MDCTraceUtils.generateNextSpanId());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:8080/hello4?name=hello4", HttpMethod.GET, httpEntity, String.class);
        log.info("hello3");
        ResponseEntity<String> exchange2 = restTemplate.exchange("http://localhost:8080/hello4?name=hello4", HttpMethod.GET, httpEntity, String.class);
        log.info("hello3");
        return "hello2, " + name + "!";
    }

    @GetMapping("/hello4")
    public String hello4(String name){
        log.info("hello4");
        return "hello4, " + name + "!";
    }
}
