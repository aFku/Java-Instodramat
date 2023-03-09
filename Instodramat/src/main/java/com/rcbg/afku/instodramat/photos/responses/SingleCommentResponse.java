package com.rcbg.afku.instodramat.photos.responses;

import com.rcbg.afku.instodramat.common.responses.MetaDataResponse;
import com.rcbg.afku.instodramat.photos.dtos.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleCommentResponse extends MetaDataResponse {

    private CommentResponseDto data;
}
