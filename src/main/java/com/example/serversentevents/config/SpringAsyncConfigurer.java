package com.example.serversentevents.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

/**
 * Pesquisado nas seguintes fontes:
 *
 * https://www.baeldung.com/spring-async
 * https://stackoverflow.com/questions/52397752/asynchronous-rest-api-generating-warning
 * https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/async-intercept.html
 *
 */
@EnableAsync
@Configuration
public class SpringAsyncConfigurer implements AsyncConfigurer, WebMvcConfigurer {

    private final HandlerInterceptor sseInterceptorHandler;

    public SpringAsyncConfigurer(HandlerInterceptor sseInterceptorHandler) {
        this.sseInterceptorHandler = sseInterceptorHandler;
    }

    @Bean
    protected WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                Optional.ofNullable(getAsyncExecutor()).ifPresent(configurer::setTaskExecutor);
            }
        };
    }

    @Override
    public AsyncTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sseInterceptorHandler);
    }
}
