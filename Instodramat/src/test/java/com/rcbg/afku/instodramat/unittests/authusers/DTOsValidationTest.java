package com.rcbg.afku.instodramat.unittests.authusers;

import com.rcbg.afku.instodramat.authusers.dtos.FollowStateDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DTOsValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testProfileDTONotValid(){
        ProfileDto validDto = new ProfileDto();
        validDto.setUserId("d8046----976-8b33-4dde-a511-b0c84----ec0fa52");
        validDto.setUsername("testuser1".repeat(30));
        validDto.setFirstName("test".repeat(64));
        validDto.setLastName("user".repeat(64));
        validDto.setBirthday(LocalDate.of(3000, 5, 10));
        validDto.setEmail("testuser@testema@.il.com");
        validDto.setGender("NOT_MALE");
        validDto.setDisplayMode("FIRSTNAME");

        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(validDto, OnCreate.class);
        Assertions.assertFalse(violations.isEmpty());
        String[] messagesList = {
                "userId : This field must be valid UUID",
                "username : Max size for this field is 255",
                "firstName : Max size for this field is 255",
                "lastName : Max size for this field is 255",
                "birthday : Date must be from past",
                "email : Provide valid email",
                "gender : This field can contain only values: [MALE, FEMALE, NOT_DEFINED]",
                "displayMode : This field can contain only values: [FULLNAME, USERNAME]"
        };
        int[] x = {1, 2, 3};
        List<String> violationsMessagesList = violations.stream().map(ConstraintViolation::getMessage).toList();
        violationsMessagesList.stream().forEach(System.out::println);
        Assertions.assertEquals(messagesList.length, violationsMessagesList.size());
        Arrays.stream(messagesList).forEach((message) -> Assertions.assertTrue(violationsMessagesList.contains(message)));
    }

    @Test
    void testProfileDTOValid(){
        ProfileDto validDto = new ProfileDto();
        validDto.setUserId("d8046976-8b33-4dde-a511-b0c84ec0fa52");
        validDto.setUsername("testuser1");
        validDto.setFirstName("test");
        validDto.setLastName("user");
        validDto.setBirthday(LocalDate.of(1999, 5, 10));
        validDto.setEmail("testuser@testemail.com");
        validDto.setGender("MALE");
        validDto.setDisplayMode("FULLNAME");

        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(validDto, OnCreate.class);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void testProfileDTOEmptyFields(){
        ProfileDto validDto = new ProfileDto();
        validDto.setUserId("");
        validDto.setLastName(" ");
        validDto.setBirthday(null);
        validDto.setEmail(null);
        validDto.setGender("");
        validDto.setDisplayMode(null);

        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(validDto, OnCreate.class);
        Assertions.assertFalse(violations.isEmpty());
        String[] messagesList = {
                "userId : This field must be valid UUID",
                "userId : This field must be valid UUID",
                "username : This field cannot be empty",
                "gender : This field can contain only values: [MALE, FEMALE, NOT_DEFINED]"
        };
        List<String> violationsMessagesList = violations.stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(messagesList.length, violationsMessagesList.size());
        Arrays.stream(messagesList).forEach((message) -> Assertions.assertTrue(violationsMessagesList.contains(message)));
    }

    @Test
    void testFollowStateDtoValid(){
        FollowStateDto followStateDto = new FollowStateDto();
        String[] states = {"FOLLOW", "UNFOLLOW"};
        for(String state: states){
            followStateDto.setState(state);
            Set<ConstraintViolation<FollowStateDto>> violations = validator.validate(followStateDto);
            Assertions.assertTrue(violations.isEmpty());
        }
    }

    @Test
    void testFollowStateDtoInvalid(){
        FollowStateDto followStateDto = new FollowStateDto();
        followStateDto.setState("FOLLOWUNFOLLOW");
        Set<ConstraintViolation<FollowStateDto>> violations = validator.validate(followStateDto);
        Assertions.assertEquals(1, violations.size());
        String message = violations.stream().map(ConstraintViolation::getMessage).toList().get(0);
        Assertions.assertEquals("state : This field can contain only values: [FOLLOW, UNFOLLOW]", message);

        followStateDto.setState(null);
        violations = validator.validate(followStateDto);
        Assertions.assertEquals(1, violations.size());
        message = violations.stream().map(ConstraintViolation::getMessage).toList().get(0);
        Assertions.assertEquals("state : This field cannot be empty", message);
    }
}
