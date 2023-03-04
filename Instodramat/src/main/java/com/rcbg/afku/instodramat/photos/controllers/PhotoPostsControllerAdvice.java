package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.authusers.controllers.ProfileController;
import com.rcbg.afku.instodramat.common.responses.ErrorResponse;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.photos.exceptions.SavePhotoException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PhotoPostsControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(PhotoPostsControllerAdvice.class);

    @ExceptionHandler({SavePhotoException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> PostCreationFailedHandler(SavePhotoException ex, HttpServletRequest request){
        logger.error("Saving photo post stopped due to exception: " + ex);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), "error");
        ErrorResponse response = new ErrorResponse(metaData);
        response.addMessage(ex.getMessage());
        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }
}
