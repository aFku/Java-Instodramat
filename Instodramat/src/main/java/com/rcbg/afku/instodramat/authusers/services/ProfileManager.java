package com.rcbg.afku.instodramat.authusers.services;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.authusers.dtos.FollowState;
import com.rcbg.afku.instodramat.authusers.dtos.FollowStateDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileMapper;
import com.rcbg.afku.instodramat.authusers.exceptions.ProfileAlreadyExists;
import com.rcbg.afku.instodramat.authusers.exceptions.ProfileFollowException;
import com.rcbg.afku.instodramat.authusers.exceptions.ProfileNotFound;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Service
@Validated
public class ProfileManager {

    private final Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    private final ProfileRepository profileRepository;

    private final JwtDecoder decoder;

    @Autowired
    public ProfileManager(ProfileRepository profileRepository, @Qualifier("instodramatJwtDecoder") JwtDecoder decoder){
        this.profileRepository = profileRepository;
        this.decoder = decoder;
    }

    public Profile getDomainObjectByProfileId(int profileId){
        return profileRepository.findProfileByProfileId(profileId).orElseThrow(() -> new ProfileNotFound("There is no profile with profileId: " + profileId));
    }

    public String profileIdToUserId(int profileId){
        return getDomainObjectByProfileId(profileId).getUserId();
    }

    public String profileIdToUsername(int profileId){
        return getDomainObjectByProfileId(profileId).getUsername();
    }

    public ProfileDto jwtToProfileDto(String jwtHeader) {
        Jwt rawJwt = decoder.decode(jwtHeader.substring("Bearer".length()).trim());
        ProfileDto dto = new ProfileDto();
        dto.setUserId(rawJwt.getClaimAsString("sub"));
        dto.setUsername(rawJwt.getClaimAsString("user_name"));
        dto.setFirstName(rawJwt.getClaimAsString("first_name"));
        dto.setLastName(rawJwt.getClaimAsString("last_name"));
        dto.setEmail(rawJwt.getClaimAsString("email"));
        return dto;
    }

    @Transactional
    @Validated(OnCreate.class)
    public ProfileDto createProfile(String jwtHeader){
        @Valid ProfileDto requestDto = this.jwtToProfileDto(jwtHeader);
        if(profileRepository.existsByUserId(requestDto.getUserId())){
            throw new ProfileAlreadyExists("Profile related to your userID already exists");
        }
        Profile profile = ProfileMapper.INSTANCE.toEntity(requestDto);
        profileRepository.save(profile);
        logger.info("New profile created for userId: " + requestDto.getUserId() + " data: " + requestDto.toString());
        return ProfileMapper.INSTANCE.toDto(profile);
    }

    @Transactional
    @Validated(OnUpdate.class)
    public ProfileDto updateProfile(@Valid ProfileDto requestDto, int profileId){
        Profile profile = getDomainObjectByProfileId(profileId);
        profile = ProfileMapper.INSTANCE.updateEntity(requestDto, profile);
        profileRepository.save(profile);
        logger.info("Profile with profileId: " + profileId + " has been updated with new values");
        return ProfileMapper.INSTANCE.toDto(profile);
    }

    @Transactional
    public void deleteProfile(int profileId){
        Profile profile = getDomainObjectByProfileId(profileId);
        profileRepository.delete(profile);
        logger.info("Profile with profileId: " + profileId + " has been deleted");
    }

    public ProfileDto getOneProfileById(int profileId){
        Profile profile = getDomainObjectByProfileId(profileId);
        return ProfileMapper.INSTANCE.toDto(profile);
    }

    public Page<ProfileDto> getPageOfProfilesBySpecification(Pageable pageable){
        return profileRepository.findAll(pageable).map(ProfileMapper.INSTANCE::toDto);
    }

    @Validated
    @Transactional
    public void setFollowProfileState(String userId, int profileId, @Valid FollowStateDto state){
        Profile profileToFollow = this.getDomainObjectByProfileId(profileId);
        Profile initiatorProfile = profileRepository.findProfileByUserId(userId).orElseThrow(() -> new ProfileNotFound("There is no profile related to userId: " + userId));
        if(profileToFollow.equals(initiatorProfile)){
            throw new ProfileFollowException("You cannot follow or unfollow yourself");
        }
        boolean alreadyFollowing = profileToFollow.getFollowers().contains(initiatorProfile);
        if(Objects.equals(state.getState(), FollowState.FOLLOW.toString())){
            if(alreadyFollowing){
                throw new ProfileFollowException("profileId: " + initiatorProfile.getProfileId() + " already follow profileId: " + profileToFollow.getProfileId());
            }
            profileToFollow.addToFollowers(initiatorProfile);
            logger.info("profileId: " + initiatorProfile.getProfileId() + " started following profileId: " + profileToFollow.getProfileId());
        } else {
            if(! alreadyFollowing){
                throw new ProfileFollowException("profileId: " + initiatorProfile.getProfileId() + " do not follow profileId: " + profileToFollow.getProfileId());
            }
            profileToFollow.removeFromFollowers(initiatorProfile);
            logger.info("profileId: " + initiatorProfile.getProfileId() + " stopped following profileId: " + profileToFollow.getProfileId());
        }
        profileRepository.save(profileToFollow);
    }

    public Page<ProfileDto> getPageOfProfilesThatGivenProfileFollows(int profileId, Pageable pageable){
        Profile profile = this.getDomainObjectByProfileId(profileId);
        return profileRepository.findByFollowersContaining(profile, pageable).map(ProfileMapper.INSTANCE::toDto);
    }

    public Page<ProfileDto> getPageOfProfilesThatAreFollowersOfGivenProfile(int profileId, Pageable pageable){
        this.getDomainObjectByProfileId(profileId);
        return profileRepository.findFollowersById(profileId, pageable).map(ProfileMapper.INSTANCE::toDto);
    }



}
