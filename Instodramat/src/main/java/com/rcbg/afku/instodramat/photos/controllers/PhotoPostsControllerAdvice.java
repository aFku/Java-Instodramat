package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.authusers.controllers.ProfileController;
import com.rcbg.afku.instodramat.common.responses.ErrorResponse;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.ResponseCreationUtils;
import com.rcbg.afku.instodramat.photos.exceptions.CommentNotFoundException;
import com.rcbg.afku.instodramat.photos.exceptions.LikeException;
import com.rcbg.afku.instodramat.photos.exceptions.PhotoNotFoundException;
import com.rcbg.afku.instodramat.photos.exceptions.SavePhotoException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;

@RestControllerAdvice
public class PhotoPostsControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(PhotoPostsControllerAdvice.class);

    @ExceptionHandler({SavePhotoException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> PostCreationFailedHandler(SavePhotoException ex, HttpServletRequest request){
        logger.error("Saving photo post stopped due to exception: " + ex);
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PhotoNotFoundException.class, CommentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> PostNotFoundHandler(RuntimeException ex, HttpServletRequest request){
        logger.error("Fetching error: " + ex);
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.NOT_FOUND,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({LikeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> LikeStateExceptionHandler(LikeException ex, HttpServletRequest request, Authentication authentication){
        logger.error("User with ID: " + authentication.getName() + " got like exception: " + ex.getMessage());
        ErrorResponse response = ResponseCreationUtils.prepareErrorResponse(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                new Link[]{},
                new ArrayList<String>(Collections.singleton(ex.getMessage()))
        );
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
