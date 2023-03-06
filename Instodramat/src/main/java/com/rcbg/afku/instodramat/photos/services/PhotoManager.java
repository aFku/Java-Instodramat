package com.rcbg.afku.instodramat.photos.services;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.responses.PageProfileResponse;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.*;
import com.rcbg.afku.instodramat.photos.exceptions.ImageUploadException;
import com.rcbg.afku.instodramat.photos.exceptions.LikeException;
import com.rcbg.afku.instodramat.photos.exceptions.PhotoNotFoundException;
import com.rcbg.afku.instodramat.photos.exceptions.SavePhotoException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Validated
public class PhotoManager {

    private final Logger logger = LoggerFactory.getLogger(PhotoManager.class);

    private final PhotoRepository repository;
    private final ProfileManager profileManager;
    private final ImageSaver imageSaver;

    @Autowired
    public PhotoManager(PhotoRepository repository, ProfileManager profileManager, ImageSaver imageSaver){
        this.repository = repository;
        this.profileManager = profileManager;
        this.imageSaver = imageSaver;
    }

    public Photo getDomainObjectByPhotoId(int photoId){
        return repository.findById(photoId).orElseThrow(() -> new PhotoNotFoundException("Cannot find photo with ID: " + photoId));
    }

    public boolean checkOwnership(int photoId, int profileId){
        Photo photo = this.getDomainObjectByPhotoId(photoId);
        Profile profile = profileManager.getDomainObjectByProfileId(profileId);
        return Objects.equals(photo.getAuthor().getProfileId(), profile.getProfileId());
    }


    @Transactional
    @Validated(OnCreate.class)
    public PhotoResponseDto createPhotoPost(@Valid PhotoRequestDto requestDto, String userId){
        LocalDateTime publishDate = LocalDateTime.now();
        try {
            String pathToSavedPhoto = imageSaver.saveMultipartFile(requestDto.getFile(), imageSaver.generateBase64Name(userId, publishDate));
            Photo requestedEntity = PhotoMapper.INSTANCE.requestDtoToEntity(requestDto, publishDate, pathToSavedPhoto, profileManager.getDomainObjectByUserId(userId));
            repository.save(requestedEntity);
            logger.info("UserId: " + userId + " created photo with ID: " + requestedEntity.getPhotoId() + " saved under path: " + pathToSavedPhoto);
            return PhotoMapper.INSTANCE.EntityToResponseDto(requestedEntity);
        } catch (ImageUploadException ex) {
            throw new SavePhotoException(ex.getMessage());
        }
    }

    @Transactional
    @Validated(OnUpdate.class)
    public PhotoResponseDto updatePhoto(@Valid PhotoRequestDto requestDto, int photoId){
        Photo photo = getDomainObjectByPhotoId(photoId);
        photo = PhotoMapper.INSTANCE.updateEntityWithRequestDto(requestDto, photo);
        repository.save(photo);
        logger.info("Photo with ID: " + photoId + " has been updated");
        return PhotoMapper.INSTANCE.EntityToResponseDto(photo);
    }

    @Transactional
    public void deletePhotoPost(int photoId){
        Photo photo = getDomainObjectByPhotoId(photoId);
        repository.delete(photo);
        logger.info("Photo with ID: " + photoId + " has been deleted");
    }

    public PhotoResponseDto getPhotoPostByPhotoId (int photoId){
        return PhotoMapper.INSTANCE.EntityToResponseDto(getDomainObjectByPhotoId(photoId));
    }

    @Validated
    @Transactional
    public void setLikeStatus(String userId, int photoId, @Valid LikeDto dto){
        Profile profileGivingLike = profileManager.getDomainObjectByUserId(userId);
        Photo photoObtainingLike = getDomainObjectByPhotoId(photoId);
        if(LikeState.valueOf(dto.getState()) == LikeState.LIKE){
            if(photoObtainingLike.getLikes().contains(profileGivingLike)){
                throw new LikeException("Photo have already like from you");
            }
            photoObtainingLike.addLike(profileGivingLike);
            logger.info("Profile ID: " + profileGivingLike.getProfileId() + " gave like to photo ID: " + photoId);
        } else {
            if(!photoObtainingLike.getLikes().contains(profileGivingLike)){
                throw new LikeException("You already dislike this photo");
            }
            photoObtainingLike.removeLike(profileGivingLike);
            logger.info("Profile ID: " + profileGivingLike.getProfileId() + " gave dislike to photo ID: " + photoId);
        }
    }

    // List with likes for given photo
    public Page<PageProfileResponse> getLikesFromPhotoId(int photoId, Pageable pageable){
        return null;
    }

    // List with photos owned by given profile (List in profile page)
    public Page<PhotoResponseDto> getAllPhotoPostsByAuthorProfileId(int profileId, Pageable pageable){
        return null;
    }

    // List All photos (Community page)
    public Page<PhotoResponseDto> getAllLatestPhotoPosts(Pageable pageable){
        return null;
    }

    // List all your photos friends (Your main page)
    public Page<PhotoResponseDto> getAllLatestPhotosFromFollowersByProfileId(int profileId, Pageable pageable){
        return null;
    }
}
