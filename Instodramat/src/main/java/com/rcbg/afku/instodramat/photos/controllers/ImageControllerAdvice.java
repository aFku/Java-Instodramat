package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.photos.exceptions.ImageNotFound;
import com.rcbg.afku.instodramat.photos.exceptions.ImageReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ImageControllerAdvice {
    private final Logger logger = LoggerFactory.getLogger(ImageControllerAdvice.class);

    @ExceptionHandler({ImageNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> ImageNotFoundHandler(ImageNotFound ex){
        logger.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ImageReadException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<String> ImageReadExceptionHandler(ImageReadException ex){
        logger.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
