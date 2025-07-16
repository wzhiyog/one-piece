package com.github.wzhiyog.springcloudlearning.httpexchange;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface GreetingHttpClient {
    @GetExchange
    String greeting();
}
