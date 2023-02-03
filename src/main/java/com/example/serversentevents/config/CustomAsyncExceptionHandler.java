package com.example.serversentevents.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@ControllerAdvice
public class CustomAsyncExceptionHandler
        implements AsyncUncaughtExceptionHandler {


    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleRestClientResponseException(RestClientResponseException e, HttpServletRequest request) {
        return ResponseEntity.status(e.getRawStatusCode()).body(e.getMessage());
    }

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        System.out.println("Exception message - " + throwable.getMessage());
        System.out.println("Method name - " + method.getName());
        for (Object param : obj) {
            System.out.println("Parameter value - " + param);
        }
    }
}