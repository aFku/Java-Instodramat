package com.rcbg.afku.instodramat.authusers.controllers;

import com.rcbg.afku.instodramat.authusers.dtos.FollowStateDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.responses.PageProfileResponse;
import com.rcbg.afku.instodramat.authusers.responses.SingleProfileResponse;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.PaginationData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileManager profileManager;

    @Autowired
    ProfileController(ProfileManager profileManager){
        this.profileManager = profileManager;
    }

    @PostMapping(value = "/create", produces = { "application/hal+json" })
    public ResponseEntity<SingleProfileResponse> createProfile(HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.CREATED.value(), "object");
        ProfileDto profileDto = profileManager.createProfile(request.getHeader("Authorization"));
        SingleProfileResponse response = new SingleProfileResponse(profileDto);
        response.setMetaData(metaData);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{profileId}", produces = { "application/hal+json" })
    public ResponseEntity<SingleProfileResponse> getOneProfileByProfileId(HttpServletRequest request, @PathVariable int profileId, Authentication authentication){
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "object");
        ProfileDto profileDto = profileManager.getOneProfileById(profileId);
        SingleProfileResponse response = new SingleProfileResponse(profileDto);
        response.setMetaData(metaData);
        if(Objects.equals(authentication.getName(), profileManager.profileIdToUserId(profileId))){
            response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).updateProfile(request, profileId, null, authentication)).withRel("update").withType("PATCH"));
            response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).deleteProfile(profileId)).withRel("delete").withType("DELETE"));
        } else {
            response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).addRequestingProfileAsFollowerOfAnother(null, profileId, authentication)).withRel("changeFollowState").withType("POST"));
        }
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PatchMapping(value = "/{profileId}", produces = { "application/hal+json" })
    @PreAuthorize("@profileManager.profileIdToUserId(#profileId) == authentication.getName()")
    public ResponseEntity<SingleProfileResponse> updateProfile(HttpServletRequest request, @PathVariable int profileId, @RequestBody ProfileDto requestDto, Authentication authentication){
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "object");
        ProfileDto profileDto = profileManager.updateProfile(requestDto, profileId);
        SingleProfileResponse response = new SingleProfileResponse(profileDto);
        response.setMetaData(metaData);
        if(Objects.equals(authentication.getName(), profileManager.profileIdToUserId(profileId))){
            response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).getOneProfileByProfileId(request, profileId, authentication)).withRel("profile").withType("GET"));
        }
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{profileId}")
    @PreAuthorize("@profileManager.profileIdToUserId(#profileId) == authentication.getName()")
    public ResponseEntity<Void> deleteProfile(@PathVariable int profileId){
        HttpHeaders headers = new HttpHeaders();
        profileManager.deleteProfile(profileId);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<PageProfileResponse> getPageOfProfiles(HttpServletRequest request, Pageable pageable){
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        Page<ProfileDto> profiles = profileManager.getPageOfProfilesBySpecification(pageable);
        PaginationData paginationData = new PaginationData(profiles);
        PageProfileResponse response = new PageProfileResponse();
        response.setMetaData(metaData);
        response.setData(profiles.getContent());
        response.setPagination(paginationData);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping("{profileId}/follows")
    public ResponseEntity<Void> addRequestingProfileAsFollowerOfAnother(@RequestBody FollowStateDto requestDto, @PathVariable int profileId, Authentication authentication){
        profileManager.setFollowProfileState(authentication.getName(), profileId, requestDto);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "{profileId}/follows", produces = { "application/hal+json" })
    public ResponseEntity<PageProfileResponse> getFollowRelatedProfiles(HttpServletRequest request, @PathVariable int profileId, Pageable pageable, @RequestParam("direction") String direction, Authentication authentication){
        Page<ProfileDto> profiles;
        if(Objects.equals(direction, FollowDirection.TO_PROFILE.toString())){
            profiles = profileManager.getPageOfProfilesThatAreFollowersOfGivenProfile(profileId, pageable);
        } else if (Objects.equals(direction, FollowDirection.FROM_PROFILE.toString())){
            profiles = profileManager.getPageOfProfilesThatGivenProfileFollows(profileId, pageable);
        } else {
            throw new IllegalArgumentException("direction parameter accepts only values: [FROM_PROFILE, TO_PROFILE]");
        }
        HttpHeaders headers = new HttpHeaders();
        MetaData metaData = new MetaData(request.getRequestURI(), HttpStatus.OK.value(), "list");
        PaginationData paginationData = new PaginationData(profiles);
        PageProfileResponse response = new PageProfileResponse();
        response.setMetaData(metaData);
        response.setData(profiles.getContent());
        response.setPagination(paginationData);
        response.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).getOneProfileByProfileId(request, profileId, authentication)).withRel("profile").withType("GET"));
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

}
