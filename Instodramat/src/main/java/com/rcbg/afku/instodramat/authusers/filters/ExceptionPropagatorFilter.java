package com.rcbg.afku.instodramat.authusers.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rcbg.afku.instodramat.authusers.controllers.ProfileAdviceController;
import com.rcbg.afku.instodramat.authusers.controllers.ProfileController;
import com.rcbg.afku.instodramat.authusers.exceptions.UserNotRegisteredException;
import com.rcbg.afku.instodramat.common.responses.ErrorResponse;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class ExceptionPropagatorFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(ExceptionPropagatorFilter.class);

    ObjectMapper mapper;

    @Autowired
    public ExceptionPropagatorFilter() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UserNotRegisteredException ex){
            logger.error("Filter Error: " + ex);
            response = prepareResponseUnregisteredUser(response, request, ex);
            return;
        }
    }

    private HttpServletResponse prepareResponseUnregisteredUser(HttpServletResponse servletResponse, HttpServletRequest request, RuntimeException ex) throws IOException {
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), "error");
        ErrorResponse response = new ErrorResponse(metaData);
        response.addMessage(ex.getMessage());
        response.setMetaData(metaData);
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).createProfile(request)).withRel("signup").withType("POST"));
        String json = mapper.writeValueAsString(response);

        servletResponse.getWriter().write(json);
        servletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
        servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        servletResponse.flushBuffer();
        return servletResponse;
    }
}
