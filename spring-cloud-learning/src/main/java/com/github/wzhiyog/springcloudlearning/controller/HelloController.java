package com.github.wzhiyog.springcloudlearning.controller;

import com.github.wzhiyog.springcloudlearning.httpexchange.GreetingHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private GreetingHttpClient greetingHttpClient;

    @GetMapping("/")
    public String greeting() {
        return "Hello, Spring Cloud!";
    }

    @GetMapping("/discovery")
    public List<String> discovery() {
        List<String> services = discoveryClient.getServices();
        services.forEach(service -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            System.out.println(instances);
        });
        return services;
    }

    @GetMapping("/loadbalance")
    public String loadbalance() {
        return restClientBuilder.build().get().uri("http://spring-cloud-learning").retrieve().body(String.class);
    }

    @GetMapping("/http-exchange")
    public String httpExchange() {
        return greetingHttpClient.greeting();
    }


}
