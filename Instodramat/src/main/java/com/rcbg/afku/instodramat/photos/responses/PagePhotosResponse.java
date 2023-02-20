package com.rcbg.afku.instodramat.photos.responses;

import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.common.responses.PaginationResponse;
import com.rcbg.afku.instodramat.photos.dtos.PhotoDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagePhotosResponse extends PaginationResponse {
    private List<PhotoResponseDto> data;
}
