package com.rcbg.afku.instodramat.photos.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDto {


    // Should be separated with requestDto or rather requestModel, but they should have common parent
    int photoId;
}
