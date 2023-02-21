package com.rcbg.afku.instodramat.photos.responses;

import com.rcbg.afku.instodramat.common.responses.MetaDataResponse;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SinglePhotoResponse extends MetaDataResponse {

    private PhotoResponseDto data;
}
