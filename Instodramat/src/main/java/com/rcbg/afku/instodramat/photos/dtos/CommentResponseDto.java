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
public class CommentResponseDto {

    int commentId;
    String content;
    LocalDateTime publishDate;
    int authorId;
    int photoId;
}
