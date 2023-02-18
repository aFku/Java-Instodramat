package com.rcbg.afku.instodramat.authusers.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class ExceptionPropagatorFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(ExceptionPropagatorFilter.class);
    private final HandlerExceptionResolver resolver;

    @Autowired
    private ExceptionPropagatorFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex){
            logger.error("Filter Error: " + ex);
            resolver.resolveException(request, response, null, ex);
        }
    }
}
