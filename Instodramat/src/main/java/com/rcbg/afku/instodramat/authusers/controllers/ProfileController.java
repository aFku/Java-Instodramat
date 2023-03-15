package com.rcbg.afku.instodramat.authusers.controllers;

import com.rcbg.afku.instodramat.authusers.dtos.FollowStateDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.responses.PageProfileResponse;
import com.rcbg.afku.instodramat.authusers.responses.ProfileResponseFactory;
import com.rcbg.afku.instodramat.authusers.responses.SingleProfileResponse;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.responses.IResponseFactory;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.PaginationData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
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

    private final IResponseFactory responseFactory = new ProfileResponseFactory();

    @Autowired
    ProfileController(ProfileManager profileManager){
        this.profileManager = profileManager;
    }

    @PostMapping(value = "/create", produces = { "application/hal+json" })
    public ResponseEntity<SingleProfileResponse> createProfile(HttpServletRequest request){
        ProfileDto profileDto = profileManager.createProfile(request.getHeader("Authorization"));
        return responseFactory.createSingleResponse(request.getRequestURI(), HttpStatus.CREATED, profileDto, new Link[]{});
    }

    @GetMapping(value = "/{profileId}", produces = { "application/hal+json" })
    public ResponseEntity<SingleProfileResponse> getOneProfileByProfileId(HttpServletRequest request, @PathVariable int profileId, Authentication authentication){
        ProfileDto profileDto = profileManager.getOneProfileById(profileId);
        Link[] hateoas;
        if(Objects.equals(authentication.getName(), profileManager.profileIdToUserId(profileId))){
            hateoas = new Link[]{
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).updateProfile(request, profileId, null, authentication)).withRel("update").withType("PATCH"),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).deleteProfile(profileId)).withRel("delete").withType("DELETE")
            };
        } else {
            hateoas = new Link[]{
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).addRequestingProfileAsFollowerOfAnother(null, profileId, authentication)).withRel("changeFollowState").withType("POST")
            };
        }
        return responseFactory.createSingleResponse(request.getRequestURI(), HttpStatus.OK, profileDto, hateoas);
    }

    @PatchMapping(value = "/{profileId}", produces = { "application/hal+json" })
    @PreAuthorize("@profileManager.profileIdToUserId(#profileId) == authentication.getName()")
    public ResponseEntity<SingleProfileResponse> updateProfile(HttpServletRequest request, @PathVariable int profileId, @RequestBody ProfileDto requestDto, Authentication authentication){
        ProfileDto profileDto = profileManager.updateProfile(requestDto, profileId);
        Link[] hateoas = new Link[0];
        if(Objects.equals(authentication.getName(), profileManager.profileIdToUserId(profileId))){
            hateoas = new Link[]{
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).getOneProfileByProfileId(request, profileId, authentication)).withRel("profile").withType("GET")
            };
        }
        return responseFactory.createSingleResponse(request.getRequestURI(), HttpStatus.OK, profileDto, hateoas);
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
        Page<ProfileDto> profiles = profileManager.getPageOfProfilesBySpecification(pageable);
        return responseFactory.createPaginationResponse(request.getRequestURI(), HttpStatus.OK, profiles, new Link[]{});
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
        Link[] hateoas = new Link[]{
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfileController.class).getOneProfileByProfileId(request, profileId, authentication)).withRel("profile").withType("GET")
        };
        return responseFactory.createPaginationResponse(request.getRequestURI(), HttpStatus.OK, profiles, hateoas);
    }

}
