package com.rcbg.afku.instodramat.photos.dtos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.photos.domain.Comment;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "commentId", ignore = true)
    @Mapping(source = "publishDate", target = "publishDate")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "photo", target = "photo")
    public Comment requestDtoToEntity(CommentRequestDto requestDto, LocalDateTime publishDate, Profile author, Photo photo);


    @InheritInverseConfiguration
    @Mapping(source = "author.profileId", target = "authorId")
    @Mapping(source = "photo.photoId", target = "photoId")
    public CommentResponseDto EntityToResponseDto(Comment comment);
}
