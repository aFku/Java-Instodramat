package com.rcbg.afku.instodramat.common.controllers;

import com.rcbg.afku.instodramat.common.responses.ErrorResponse;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.ResponseCreationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;

@RestControllerAdvice
public class GeneralControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GeneralControllerAdvice.class);

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request){
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                new Link[]{},
                ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList()
        );
        logger.error("Constraint violation from userId: " + request.getUserPrincipal().getName() + " content: " + response.toString());
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        logger.error("Illegal argument from userId: " + request.getUserPrincipal().getName() + " content: " + response.toString());
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
