package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.photos.dtos.LikeDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoRequestDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import com.rcbg.afku.instodramat.photos.responses.SinglePhotoResponse;
import com.rcbg.afku.instodramat.photos.services.PhotoManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoPostsController {

    private final PhotoManager photoManager;

    private final ProfileManager profileManager;

    @Autowired
    public PhotoPostsController(PhotoManager photoManager, ProfileManager profileManager) {
        this.photoManager = photoManager;
        this.profileManager = profileManager;
    }

    @PostMapping(consumes = MediaType.ALL_VALUE) // Temporary All for developing
    public ResponseEntity<SinglePhotoResponse> addPostWithPhoto(HttpServletRequest request, @ModelAttribute PhotoRequestDto requestDto, Authentication authentication){
        String userId = authentication.getName();
        PhotoResponseDto responseDto = photoManager.createPhotoPost(requestDto, userId);

        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.CREATED.value(), "object");
        SinglePhotoResponse response = new SinglePhotoResponse();
        response.setMetaData(metaData);
        response.setData(responseDto);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{photoId}", produces = { "application/hal+json" })
    @PreAuthorize("@photoManager.checkOwnership(#photoId, @profileManager.getDomainObjectByUserId(authentication.getName()).getProfileId())")
    public ResponseEntity<SinglePhotoResponse> updatePhoto(HttpServletRequest request, @ModelAttribute PhotoRequestDto requestDto, Authentication authentication, @PathVariable("photoId") int photoId){
        PhotoResponseDto responseDto = photoManager.updatePhoto(requestDto, photoId);

        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "object");
        SinglePhotoResponse response = new SinglePhotoResponse();
        response.setMetaData(metaData);
        response.setData(responseDto);
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PhotoPostsController.class).getPhoto(request, photoId, authentication)).withRel("photo").withType("GET"));
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/{photoId}", produces = { "application/hal+json" })
    public ResponseEntity<SinglePhotoResponse> getPhoto(HttpServletRequest request, @PathVariable("photoId") int photoId, Authentication authentication){
        PhotoResponseDto responseDto = photoManager.getPhotoPostByPhotoId(photoId);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "object");
        SinglePhotoResponse response = new SinglePhotoResponse();
        response.setMetaData(metaData);
        response.setData(responseDto);
        if(responseDto.getAuthorId() == profileManager.getDomainObjectByUserId(authentication.getName()).getProfileId()){
            response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PhotoPostsController.class).updatePhoto(request, null, authentication, photoId)).withRel("update").withType("PATCH"));
            response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PhotoPostsController.class).deletePhoto(photoId)).withRel("delete").withType("DELETE"));
        }
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{photoId}")
    @PreAuthorize("@photoManager.checkOwnership(#photoId, @profileManager.getDomainObjectByUserId(authentication.getName()).getProfileId())")
    public ResponseEntity<Void> deletePhoto(@PathVariable("photoId") int photoId){
        photoManager.deletePhotoPost(photoId);
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{photoId}/likes")
    public ResponseEntity<Void> changeLikeStatus(@PathVariable("photoId") int photoId, @RequestBody LikeDto dto, Authentication authentication){
        photoManager.setLikeStatus(authentication.getName(), photoId, dto);
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
    }

}
