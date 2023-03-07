package com.rcbg.afku.instodramat.photos.dtos;

import com.rcbg.afku.instodramat.common.validators.ValueOfEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    @NotNull(message = "state : This field cannot be empty")
    @ValueOfEnum(enumClass = LikeState.class, message = "state : This field can contain only values: [LIKE, DISLIKE]")
    private String state;
}
