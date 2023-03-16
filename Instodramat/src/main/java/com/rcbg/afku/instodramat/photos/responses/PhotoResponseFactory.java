package com.rcbg.afku.instodramat.photos.responses;

import com.rcbg.afku.instodramat.common.responses.IResponseFactory;
import com.rcbg.afku.instodramat.common.responses.ResponseCreationUtils;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PhotoResponseFactory implements IResponseFactory {
    @Override
    public ResponseEntity<PagePhotosResponse> createPaginationResponse(String uri, HttpStatus status, Page page, Link[] hateoas) {
        PagePhotosResponse response = (PagePhotosResponse) ResponseCreationUtils.preparePaginationResponse(
                new PagePhotosResponse(page.getContent()), uri, status, page, hateoas);
        return new ResponseEntity<>(response, new HttpHeaders(), status.value());
    }

    @Override
    public ResponseEntity<SinglePhotoResponse> createSingleResponse(String uri, HttpStatus status, Object data, Link[] hateoas) {
        SinglePhotoResponse response = (SinglePhotoResponse) ResponseCreationUtils.prepareSingleResponse(
                new SinglePhotoResponse((PhotoResponseDto) data), uri, status, hateoas);
        return new ResponseEntity<>(response, new HttpHeaders(), status.value());
    }
}
