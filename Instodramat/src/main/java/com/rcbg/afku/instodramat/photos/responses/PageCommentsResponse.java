package com.rcbg.afku.instodramat.photos.responses;

import com.rcbg.afku.instodramat.common.responses.PaginationResponse;
import com.rcbg.afku.instodramat.photos.dtos.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageCommentsResponse extends PaginationResponse {
    List<CommentResponseDto> data;
}
