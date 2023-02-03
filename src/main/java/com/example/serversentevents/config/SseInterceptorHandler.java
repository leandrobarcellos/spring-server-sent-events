package com.example.serversentevents.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Pesquisado nas seguintes fontes:
 *
 * https://www.baeldung.com/spring-async
 * https://stackoverflow.com/questions/52397752/asynchronous-rest-api-generating-warning
 * https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/async-intercept.html
 *
 */
@Component
public class SseInterceptorHandler implements AsyncHandlerInterceptor {

    private static final String PARAM_SSE_KEY = "sseKey";
    private static final Logger LOGGER = LoggerFactory.getLogger(SseInterceptorHandler.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.info("Handling: {}", handler.getClass());
        if (handler instanceof HandlerMethod) {
            Method m = ((HandlerMethod) handler).getMethod();
            String[] roles = Optional.ofNullable(m.getAnnotation(RolesAllowedAsync.class)).map(RolesAllowedAsync::value).orElse(null);
            if (Objects.nonNull(roles)) {
                if (Objects.nonNull(m.getParameters())) {
                    List<Parameter> parameters = Arrays.stream(m.getParameters()).filter(p -> p.isAnnotationPresent(RequestParam.class)).toList();
                    if (parameters.isEmpty()) {
                        throw new RuntimeException("O parametro de sseKey requerido está faltando.");
                    }
                    boolean found = false;
                    String sseKey = null;
                    var it = parameters.iterator();
                    do {
                        Parameter p = it.next();
                        if (PARAM_SSE_KEY.equals(p.getAnnotation(RequestParam.class).value())) {
                            found = true;
                            sseKey = request.getParameter(PARAM_SSE_KEY);
                        }
                    } while (it.hasNext() && !found);
                    if(!found){
                        throw new RuntimeException("O parametro de sseKey requerido está faltando.");
                    }
                    if(Objects.isNull(sseKey) || sseKey.isEmpty() || sseKey.isBlank()){
                        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "É necessário enviar um valor valido para o parâmetro sseKey.");
                    }
                    LOGGER.info("sseKey: {}", sseKey);
                }
            }
        }
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }
}
