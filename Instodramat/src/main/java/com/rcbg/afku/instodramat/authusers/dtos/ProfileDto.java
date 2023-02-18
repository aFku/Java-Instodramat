package com.rcbg.afku.instodramat.authusers.dtos;

import com.rcbg.afku.instodramat.authusers.domain.DisplayMode;
import com.rcbg.afku.instodramat.authusers.domain.Gender;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import com.rcbg.afku.instodramat.common.validators.ValueOfEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto extends RepresentationModel<ProfileDto> {

    // Add here Schema read-only
    @Null(message = "profileId : You cannot update profileId", groups = {OnCreate.class, OnUpdate.class})
    Integer profileId;
    @Null(message = "userId : You cannot update userId", groups = OnUpdate.class)
    @NotBlank(message = "userId : This field cannot be empty", groups = OnCreate.class)
    @UUID(message = "userId : This field must be valid UUID", groups = OnCreate.class)
    String userId;
    @NotBlank(message = "username : This field cannot be empty", groups = OnCreate.class)
    @Null(message = "username : You cannot update username", groups = OnUpdate.class)
    @Size(max = 255, message = "username : Max size for this field is {max}", groups = OnCreate.class)
    String username;
    @Size(max = 255, message = "firstName : Max size for this field is {max}", groups = {OnCreate.class, OnUpdate.class})
    String firstName;
    @Size(max = 255, message = "lastName : Max size for this field is {max}", groups = {OnCreate.class, OnUpdate.class})
    String lastName;
    @Past(message = "birthday : Date must be from past", groups = {OnCreate.class, OnUpdate.class})
    LocalDate birthday;
    @Email(message = "email : Provide valid email", groups = {OnCreate.class, OnUpdate.class})
    String email;
    @ValueOfEnum(enumClass = Gender.class, message = "gender : This field can contain only values: [MALE, FEMALE, NOT_DEFINED]", groups = {OnCreate.class, OnUpdate.class})
    String gender = "NOT_DEFINED";
    @ValueOfEnum(enumClass = DisplayMode.class, message = "displayMode : This field can contain only values: [FULLNAME, USERNAME]", groups = {OnCreate.class, OnUpdate.class})
    String displayMode = "USERNAME";

    public void setDisplayMode(String displayMode){
        if(displayMode != null){
            this.displayMode = displayMode;
        }
    }

    public void setGender(String gender){
        if(gender != null){
            this.gender = gender;
        }
    }

    @Override
    public String toString(){
        return "{\"profileId\": \"" + profileId + "\", \"userId\": \"" + userId + "\", username\": \"" + username+ "\", email\": \"" + email+ "\"}";
    }

}
