package com.github.goodluckwu.onepiece.validation;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/")
public class ValidationController {
    @GetMapping
    public String hello(@NotEmpty @RequestParam String name){
        return "hello world!" + name;
    }
}
