package com.rcbg.afku.instodramat.authusers.dtos;

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
public class FollowStateDto {

    @NotNull(message = "state : This field cannot be empty")
    @ValueOfEnum(enumClass = FollowState.class, message = "state : This field can contain only values: [FOLLOW, UNFOLLOW]")
    private String state;
}
