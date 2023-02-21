package com.rcbg.afku.instodramat.photos.dtos;

import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRequestDto {

    @Null(message = "image : you cannot update existing photo file", groups = OnUpdate.class)
    @NotNull(message = "image : image cannot be null", groups = OnCreate.class)
    private MultipartFile image;

    @Size(max = 255, message = "description : max size for description is 255 characters")
    private String description;
}
