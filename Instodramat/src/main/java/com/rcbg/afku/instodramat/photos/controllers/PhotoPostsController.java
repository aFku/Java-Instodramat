package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.PhotoDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import com.rcbg.afku.instodramat.photos.exceptions.ImageUploadException;
import com.rcbg.afku.instodramat.photos.exceptions.SavePhotoException;
import com.rcbg.afku.instodramat.photos.responses.SinglePhotoResponse;
import com.rcbg.afku.instodramat.photos.services.ImageSaver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/photos")
public class PhotoPostsController {

    private final ImageSaver imageSaver;

    private final PhotoRepository repository;

    @Autowired
    public PhotoPostsController(ImageSaver imageSaver, PhotoRepository repository) {
        this.imageSaver = imageSaver;
        this.repository = repository;
    }

    @PostMapping(consumes = MediaType.ALL_VALUE) // Temporary All for developing
    public ResponseEntity<SinglePhotoResponse> addPostWithPhoto(HttpServletRequest request, @ModelAttribute PhotoDto requestDto, Authentication authentication){
        String userId = authentication.getName();
        LocalDate date = LocalDate.now();
        Photo newPhoto = new Photo();

        // Create and Delete should be hidden in service layer, but it should look like this
        repository.save(newPhoto);

        try {
            imageSaver.saveMultipartFile(requestDto.getMultipartFile(), imageSaver.generateBase64Name(userId, date, newPhoto.getPhotoId()));
        } catch (ImageUploadException ex) {
            repository.delete(newPhoto);
            throw new SavePhotoException(ex.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.CREATED.value(), "object");
        SinglePhotoResponse response = new SinglePhotoResponse();
        response.setData(new PhotoResponseDto(newPhoto.getPhotoId()));
        response.setMetaData(metaData);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

}
