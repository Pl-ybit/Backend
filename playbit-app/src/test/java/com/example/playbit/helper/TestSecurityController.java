package com.example.playbit.helper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecurityController {

    @GetMapping("/api/v1/test/secured")
    String secured() {
        return "ok";
    }
}
