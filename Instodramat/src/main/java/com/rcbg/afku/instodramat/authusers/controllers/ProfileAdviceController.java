package com.rcbg.afku.instodramat.authusers.controllers;

import com.rcbg.afku.instodramat.authusers.exceptions.ProfileAlreadyExists;
import com.rcbg.afku.instodramat.authusers.exceptions.ProfileFollowException;
import com.rcbg.afku.instodramat.authusers.exceptions.ProfileNotFound;
import com.rcbg.afku.instodramat.authusers.exceptions.UserNotRegisteredException;
import com.rcbg.afku.instodramat.common.responses.ErrorResponse;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.ResponseCreationUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;

@RestControllerAdvice
public class ProfileAdviceController {

    private final Logger logger = LoggerFactory.getLogger(ProfileAdviceController.class);

    private final JwtDecoder decoder;

    @Autowired
    public ProfileAdviceController(JwtDecoder decoder){
        this.decoder = decoder;
    }

    @ExceptionHandler({UserNotRegisteredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> UserNotRegisteredHandler(UserNotRegisteredException ex, HttpServletRequest request){
        String jwt = request.getHeader("Authorization").substring("Bearer".length()).trim();
        String userId = decoder.decode(jwt).getSubject();
        logger.error("Unauthorized access by userId: " + userId + ", user does not have a profile. URI: " + request.getRequestURI());

        Link[] hateoas = {
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).createProfile(request)).withRel("signup").withType("POST")
        };
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED,
                hateoas,
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ProfileNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> ProfileNotFoundHandler(ProfileNotFound ex, HttpServletRequest request){
        logger.error("Got request resulting: Profile not found - " + request.getRequestURI());
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.NOT_FOUND,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ProfileAlreadyExists.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> ProfileAlreadyExistsHandler(ProfileAlreadyExists ex, HttpServletRequest request){
        logger.error("Got request resulting: Profile duplication request - " + request.getRequestURI());
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ProfileFollowException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> ProfileFollowExceptionHandler(ProfileFollowException ex, HttpServletRequest request){
        logger.error("Got request resulting: Follow exception - " + request.getRequestURI());
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
