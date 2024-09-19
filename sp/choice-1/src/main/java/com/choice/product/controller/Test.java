package com.choice.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    @GetMapping("/test")
    public String test(@RequestParam(name="page", defaultValue = "1") Integer page) {
        System.out.println("test" + page);
        return "test";
    }

}
