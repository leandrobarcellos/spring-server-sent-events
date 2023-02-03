package com.example.serversentevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAsync
@EnableWebMvc
@EnableScheduling
@SpringBootApplication
public class ServerSentEventsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerSentEventsApplication.class, args);
    }
}
