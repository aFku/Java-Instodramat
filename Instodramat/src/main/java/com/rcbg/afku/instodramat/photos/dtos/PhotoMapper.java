package com.rcbg.afku.instodramat.photos.dtos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhotoMapper {

    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @Mapping(target = "photoId", ignore = true)
    @Mapping(source = "publishDate", target = "publishDate")
    @Mapping(source = "pathToFile", target = "pathToFile")
    @Mapping(source = "author", target = "author")
    public Photo requestDtoToEntity(PhotoRequestDto requestDto, LocalDateTime publishDate, String pathToFile, Profile author);

    @InheritInverseConfiguration
    @Mapping(source = "author.profileId", target = "authorId")
    @Mapping(source = "pathToFile", target = "image")
    public PhotoResponseDto EntityToResponseDto(Photo photo);

    @Mapping(target = "photoId", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "pathToFile", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "likes", ignore = true)
    public Photo updateEntityWithRequestDto(PhotoRequestDto requestDto, @MappingTarget Photo photo);
}
