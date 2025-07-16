package com.github.wzhiyog.springcloudlearning.httpexchange;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class GreetingHttpClientConfig {
    @Bean
    public GreetingHttpClient greetingHttpClient(RestClient.Builder restclientBuilder) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(restclientBuilder.baseUrl("http://spring-cloud-learning").build()))
                .build()
                .createClient(GreetingHttpClient.class);
    }
}
