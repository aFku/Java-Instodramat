package com.rcbg.afku.instodramat.photos.services;

import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PhotoManager {

    private Logger logger = LoggerFactory.getLogger(PhotoManager.class);

    private PhotoRepository repository;

    @Autowired
    public PhotoManager(PhotoRepository repository){
        this.repository = repository;
    }


    @Transactional
    public PhotoResponseDto createPhotoPost(){
        return null;
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
