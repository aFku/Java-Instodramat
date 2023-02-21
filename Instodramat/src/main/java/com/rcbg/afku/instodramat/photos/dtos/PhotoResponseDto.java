package com.rcbg.afku.instodramat.photos.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDto {

    private int photoId;
    private LocalDateTime publishDate;
    private String description;
    private String image;
    private int authorId;
}
