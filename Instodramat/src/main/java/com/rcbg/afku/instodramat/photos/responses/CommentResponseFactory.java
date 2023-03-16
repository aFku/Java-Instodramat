package com.rcbg.afku.instodramat.photos.responses;

import com.rcbg.afku.instodramat.common.responses.IResponseFactory;
import com.rcbg.afku.instodramat.common.responses.ResponseCreationUtils;
import com.rcbg.afku.instodramat.photos.dtos.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CommentResponseFactory implements IResponseFactory {
    @Override
    public ResponseEntity<PageCommentsResponse> createPaginationResponse(String uri, HttpStatus status, Page page, Link[] hateoas) {
        PageCommentsResponse response = (PageCommentsResponse) ResponseCreationUtils.preparePaginationResponse(
                new PageCommentsResponse(page.getContent()), uri, status, page, hateoas);
        return new ResponseEntity<>(response, new HttpHeaders(), status.value());
    }

    @Override
    public ResponseEntity<SingleCommentResponse> createSingleResponse(String uri, HttpStatus status, Object data, Link[] hateoas) {
        SingleCommentResponse response = (SingleCommentResponse) ResponseCreationUtils.prepareSingleResponse(
                new SingleCommentResponse((CommentResponseDto) data), uri, status, hateoas);
        return new ResponseEntity<>(response, new HttpHeaders(), status.value());
    }
}
