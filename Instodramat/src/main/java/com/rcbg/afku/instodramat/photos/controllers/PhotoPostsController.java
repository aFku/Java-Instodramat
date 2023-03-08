package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.responses.PageProfileResponse;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.PaginationData;
import com.rcbg.afku.instodramat.photos.dtos.*;
import com.rcbg.afku.instodramat.photos.responses.PageCommentsResponse;
import com.rcbg.afku.instodramat.photos.responses.PagePhotosResponse;
import com.rcbg.afku.instodramat.photos.responses.SingleCommentResponse;
import com.rcbg.afku.instodramat.photos.responses.SinglePhotoResponse;
import com.rcbg.afku.instodramat.photos.services.CommentManager;
import com.rcbg.afku.instodramat.photos.services.PhotoManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final CommentManager commentManager;

    @Autowired
    public PhotoPostsController(PhotoManager photoManager, ProfileManager profileManager, CommentManager commentManager) {
        this.photoManager = photoManager;
        this.profileManager = profileManager;
        this.commentManager = commentManager;
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

    @GetMapping("/{photoId}/likes")
    public ResponseEntity<PageProfileResponse> getLikeList(HttpServletRequest request, @PathVariable("photoId") int photoId, Pageable pageable){
        Page<ProfileDto> data = photoManager.getLikesFromPhotoId(photoId, pageable);
        PaginationData paginationData = new PaginationData(data);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        PageProfileResponse response = new PageProfileResponse();
        response.setMetaData(metaData);
        response.setPagination(paginationData);
        response.setData(data.getContent());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<PagePhotosResponse> getPhotosOfProfile(HttpServletRequest request, @PathVariable("profileId") int profileId, Pageable pageable) {
        Page<PhotoResponseDto> data = photoManager.getAllPhotoPostsByAuthorProfileId(profileId, pageable);
        PaginationData paginationData = new PaginationData(data);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        PagePhotosResponse response = new PagePhotosResponse();
        response.setMetaData(metaData);
        response.setPagination(paginationData);
        response.setData(data.getContent());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PagePhotosResponse> getAllPhotos(HttpServletRequest request, Pageable pageable) {
        Page<PhotoResponseDto> data = photoManager.getAllLatestPhotoPosts(pageable);
        PaginationData paginationData = new PaginationData(data);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        PagePhotosResponse response = new PagePhotosResponse();
        response.setMetaData(metaData);
        response.setPagination(paginationData);
        response.setData(data.getContent());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/profile/{profileId}/followers")
    public ResponseEntity<PagePhotosResponse> getPhotosOfFollowers(HttpServletRequest request, @PathVariable("profileId") int profileId, Pageable pageable) {
        Page<PhotoResponseDto> data = photoManager.getAllLatestPhotosFromFollowersByProfileId(profileId, pageable);
        PaginationData paginationData = new PaginationData(data);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        PagePhotosResponse response = new PagePhotosResponse();
        response.setMetaData(metaData);
        response.setPagination(paginationData);
        response.setData(data.getContent());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{photoId}/comments")
    public ResponseEntity<PageCommentsResponse> getCommentsFromPhoto(HttpServletRequest request, @PathVariable("photoId") int photoId, Pageable pageable){
        Page<CommentResponseDto> data = commentManager.getCommentsFromPhoto(photoId, pageable);
        PaginationData paginationData = new PaginationData(data);
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        PageCommentsResponse response = new PageCommentsResponse();
        response.setData(data.getContent());
        response.setMetaData(metaData);
        response.setPagination(paginationData);
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PhotoPostsController.class).getPhoto(request, photoId, null)).withRel("photo").withType("GET"));
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping("/{photoId}/comments")
    public ResponseEntity<SingleCommentResponse> createCommentForPhoto(HttpServletRequest request, @PathVariable("photoId") int photoId, @RequestBody CommentRequestDto requestDto, Authentication authentication){
        CommentResponseDto data = commentManager.createCommentForPhoto(requestDto, photoId, authentication.getName());
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.CREATED.value(), "object");
        SingleCommentResponse response = new SingleCommentResponse();
        response.setData(data);
        response.setMetaData(metaData);
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PhotoPostsController.class).getPhoto(request, photoId, null)).withRel("photo").withType("GET"));
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{photoId}/comments/{commentId}")
    @PreAuthorize("@commentManager.checkOwnership(commentId, authentication.getName())")
    public ResponseEntity<Void> deleteComment(@PathVariable("photoId") int photoId, @PathVariable("commentId") int commentId){
        commentManager.deleteCommentFromPhotoById(photoId, commentId);
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
    }
}
