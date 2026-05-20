package com.example.playbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlaybitApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlaybitApplication.class, args);
    }
}
