package com.rcbg.afku.instodramat.authusers.responses;

import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.common.responses.IResponseFactory;
import com.rcbg.afku.instodramat.common.responses.MetaData;
import com.rcbg.afku.instodramat.common.responses.PaginationData;
import com.rcbg.afku.instodramat.common.responses.ResponseCreationUtils;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ProfileResponseFactory implements IResponseFactory {
    @Override
    public ResponseEntity<PageProfileResponse> createPaginationResponse(String uri, HttpStatus status, Page page, Link[] hateoas) {
        PageProfileResponse response = (PageProfileResponse) ResponseCreationUtils.preparePaginationResponse(
                new PageProfileResponse(page.getContent()), uri, status, page, hateoas);
        return new ResponseEntity<>(response, new HttpHeaders(), status.value());
    }

    @Override
    public ResponseEntity<SingleProfileResponse> createSingleResponse(String uri, HttpStatus status, Object data, Link[] hateoas) {
        SingleProfileResponse response = (SingleProfileResponse) ResponseCreationUtils.prepareSingleResponse(
                new SingleProfileResponse((ProfileDto) data), uri, status, hateoas);
        return new ResponseEntity<>(response, new HttpHeaders(), status.value());
    }
}
