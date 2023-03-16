package com.rcbg.afku.instodramat.common.responses;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IResponseFactory {
    public ResponseEntity createPaginationResponse(String uri, HttpStatus status, Page page, Link[] hateoas);

    public ResponseEntity createSingleResponse(String uri, HttpStatus status, Object data, Link[] hateoas);
}
