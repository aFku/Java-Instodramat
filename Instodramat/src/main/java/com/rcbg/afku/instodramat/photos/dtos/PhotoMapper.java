package com.rcbg.afku.instodramat.photos.dtos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhotoMapper {

    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @Mapping(target = "photoId", ignore = true)
    public Photo requestDtoToEntity(PhotoRequestDto requestDto, LocalDateTime publishDate, String pathToFile, Profile author);

    public PhotoResponseDto EntityToResponseDto(Photo photo);

    @Mapping(target = "photoId", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "pathToFile", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "likes", ignore = true)
    public Photo updateEntityWithRequestDto(PhotoRequestDto requestDto);
}
