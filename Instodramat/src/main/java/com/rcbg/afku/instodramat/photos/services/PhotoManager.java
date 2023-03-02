package com.rcbg.afku.instodramat.photos.services;

import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.PhotoMapper;
import com.rcbg.afku.instodramat.photos.dtos.PhotoRequestDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import com.rcbg.afku.instodramat.photos.exceptions.ImageUploadException;
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


    @Transactional
    @Validated(OnCreate.class)
    public PhotoResponseDto createPhotoPost(@Valid PhotoRequestDto requestDto, String userId){
        LocalDateTime publishDate = LocalDateTime.now();
        try {
            String pathToSavedPhoto = imageSaver.saveMultipartFile(requestDto.getImage(), imageSaver.generateBase64Name(userId, publishDate));
            Photo requestedEntity = PhotoMapper.INSTANCE.requestDtoToEntity(requestDto, publishDate, pathToSavedPhoto, profileManager.getDomainObjectByUserId(userId));
            repository.save(requestedEntity);
            logger.info("UserId: " + userId + " created photo with ID: " + requestedEntity.getPhotoId() + " saved under path: " + pathToSavedPhoto);
            return PhotoMapper.INSTANCE.EntityToResponseDto(requestedEntity);
        } catch (ImageUploadException ex) {
            throw new SavePhotoException(ex.getMessage());
        }
    }

    @Transactional
    public PhotoResponseDto updatePhoto(){
        return null;
    }

    @Transactional
    public void deletePhotoPost(){
        //
    }

    public PhotoResponseDto getPhotoPostByAuthorProfileId(int profileId){
        return null;
    }

    public Page<PhotoResponseDto> getAllPhotoPostsByAuthorProfileId(int profileId, Pageable pageable){
        return null;
    }

    public Page<PhotoResponseDto> getAllLatestPhotoPosts(Pageable pageable){
        return null;
    }

    public Page<PhotoResponseDto> getAllLatestPhotosFromFollowersByProfileId(int profileId, Pageable pageable){
        return null;
    }
}
