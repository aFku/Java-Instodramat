package com.rcbg.afku.instodramat.common.responses;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ResponseCreationUtils {

    public static PaginationResponse preparePaginationResponse(PaginationResponse response, String uri, HttpStatus status, Page page, Link[] hateoas){
        MetaData metaData = new MetaData(uri, status.value(), "list");
        PaginationData paginationData = new PaginationData(page);
        response.setPagination(paginationData);
        response.setMetaData(metaData);
        for(Link link : hateoas){
            response.add(link);
        }
        return response;
    }

    public static MetaDataResponse prepareSingleResponse(MetaDataResponse response, String uri, HttpStatus status, Link[] hateoas){
        MetaData metaData = new MetaData(uri, status.value(), "object");
        response.setMetaData(metaData);
        for(Link link : hateoas){
            response.add(link);
        }
        return response;
    }

    public static ErrorResponse prepareErrorResponse(String uri, HttpStatus status, Link[] hateoas, List<String> messages){
        ErrorResponse response = new ErrorResponse();
        MetaData metaData = new MetaData(uri, status.value(), "error");
        response.setMetaData(metaData);
        response.setMessages(messages);
        for(Link link : hateoas){
            response.add(link);
        }
        return response;
    }
}
