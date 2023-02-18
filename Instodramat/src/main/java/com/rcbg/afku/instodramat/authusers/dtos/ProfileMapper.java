package com.rcbg.afku.instodramat.authusers.dtos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @InheritInverseConfiguration
    @Mapping(target = "profileId", ignore = true)
    public Profile toEntity(ProfileDto dto);

    public ProfileDto toDto(Profile profile);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "profileId", ignore = true)
    public Profile updateEntity(ProfileDto dto, @MappingTarget Profile profile);
}
