package com.rcbg.afku.instodramat.unittests.authusers;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileMapper;
import com.rcbg.afku.instodramat.authusers.exceptions.ProfileNotFound;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import jakarta.validation.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.validation.annotation.Validated;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ProfileManagerTest {

    @Mock(name = "instodramatJwtDecoder")
    JwtDecoder decoder;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileManager profileManager;

    private static Validator validator;

    private String testUserJwt = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDNE53MExpdW1DQXZpV0x1TzdMN2J1VkFqYXVzbmJjODc0YjNzZjFGa21JIn0." +
            "eyJleHAiOjE2NzU4NjQ4MzcsImlhdCI6MTY3NTg2NDUzNywianRpIjoiZjg5ODdhODEtMjllOC00ZDJkLWJiMmQtNzQyNzNhZjA2OTdkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4" +
            "MDgxL2F1dGgvcmVhbG1zL2luc3RvZHJhbWF0IiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImQ4MDQ2OTc2LThiMzMtNGRkZS1hNTExLWIwYzg0ZWMwZmE1MiIsInR5cCI6IkJlYXJlciIsImF6" +
            "cCI6Imluc3RvZHJhbWF0Iiwic2Vzc2lvbl9zdGF0ZSI6IjllNDMwNmJjLTM0M2ItNDg3Yi1hNzA2LWIyMjliY2U0ODBhMSIsImFjciI6IjEiLCJzY29wZSI6InVzZXJEZXRhaWxzIiwic2lk" +
            "IjoiOWU0MzA2YmMtMzQzYi00ODdiLWE3MDYtYjIyOWJjZTQ4MGExIiwidXNlcl9uYW1lIjoidGVzdHVzZXIxIiwibGFzdF9uYW1lIjoidXNlciIsImZpcnN0X25hbWUiOiJ0ZXN0IiwiZW1ha" +
            "WwiOiJ0ZXN0dXNlckB0ZXN0ZW1haWwuY29tIn0.hbBvsnEBgRsfZAyaYlmSrM87kop99grhKW-jtKIIUFhZv2ED-ViNYk5Opg-H3SsQmvW2cw95UkN1FTf1EZdA-E5Rb9MsgPVdd7hNrCo7jCQ" +
            "uAPNFn8DwusWHL5b0BkmcSl1fn9zKI8ymacIUbUxhIkn7WaQVhr0Idm9S2S-V9rKnP6lqw4e6tRrjA6LA53ngFB5aXCrcrMZD-GWjVLGo2GuP0ZL-8YsLA2WT4iggj_J4RhTEFE2JzoqHEbpKp" +
            "LpPzDe2-PJBw82402ahf8jckh6n_7uXEK9DoZntxKLXafcPm3-WM-XdpnKZFxp1rshJVtDop7SAdr0lmq3IxfNWnA";

    private ProfileDto generateBaseProfileDto(){
        ProfileDto requestDto = new ProfileDto();
        requestDto.setUserId("bfcd38f2-81ed-43be-94cf-9341ae60840c");
        requestDto.setUsername("testuser1");
        requestDto.setFirstName("test");
        requestDto.setLastName("user");
        requestDto.setBirthday(null);
        requestDto.setEmail("testuser@testemail.com");
        requestDto.setGender("NOT_DEFINED");
        requestDto.setDisplayMode("USERNAME");
        return requestDto;
    }

    private Jwt prepareJwt(String tokenValue, String sub, String userName, String firstName, String lastName, String email){
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(10);
        Map<String, Object> headers = new HashMap<>();
        headers.put("random", "header");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("user_name", userName);
        claims.put("first_name", firstName);
        claims.put("last_name", lastName);
        claims.put("email", email);
        return new Jwt(tokenValue, issuedAt, expiresAt, headers, claims);
    }

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void getDomainObjectByProfileIdTest(){
        // Exist
        Profile existingProfile = new Profile();
        Mockito.when(profileRepository.findProfileByProfileId(1)).thenReturn(Optional.of(existingProfile));
        Assertions.assertEquals(existingProfile, profileManager.getDomainObjectByProfileId(1));

        // Not exist
        Mockito.when(profileRepository.findProfileByProfileId(2)).thenReturn(Optional.empty());
        Assertions.assertThrows(ProfileNotFound.class, () -> profileManager.getDomainObjectByProfileId(2));
    }

    @Test
    public void profileIdConvertersTest(){
        // To UserID
        Profile profileUserId = new Profile();
        profileUserId.setProfileId(1);
        profileUserId.setUserId("UUID-UUID-123");
        Mockito.when(profileRepository.findProfileByProfileId(1)).thenReturn(Optional.of(profileUserId));
        Assertions.assertEquals("UUID-UUID-123", profileManager.profileIdToUserId(1));

        // To Username
        Profile profileUsername = new Profile();
        profileUsername.setProfileId(2);
        profileUsername.setUsername("testusername123");
        Mockito.when(profileRepository.findProfileByProfileId(2)).thenReturn(Optional.of(profileUsername));
        Assertions.assertEquals("testusername123", profileManager.profileIdToUsername(2));
    }

    @Test
    @Validated(OnCreate.class)
    public void createProfileSuccessTest(){
        ProfileDto requestDto = generateBaseProfileDto();
        Profile profile = ProfileMapper.INSTANCE.toEntity(requestDto);
        Mockito.when(profileRepository.save(Mockito.any(Profile.class))).thenAnswer(i -> i.getArguments()[0]);
        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(requestDto, OnCreate.class);
        violations.stream().forEach(System.out::println);
        Assertions.assertTrue(violations.isEmpty());
        Jwt mockedJwt = prepareJwt(this.testUserJwt,
                requestDto.getUserId(),
                requestDto.getUsername(),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getEmail());
        Mockito.when(decoder.decode(Mockito.any())).thenReturn(mockedJwt);
        ProfileDto createdProfileDto = profileManager.createProfile(this.testUserJwt);
        assertThat(createdProfileDto).usingRecursiveComparison().isEqualTo(ProfileMapper.INSTANCE.toDto(profile));
    }

    @Test
    public void createProfileFailTest(){
        ProfileDto requestDto = new ProfileDto();
        requestDto.setUserId("bfcd38f2-81ed-43be-94cf-9341ae6089-78-976ed-40c");
        requestDto.setUsername("");
        requestDto.setFirstName(null);
        requestDto.setBirthday(LocalDate.of(2100, 2, 15));
        requestDto.setEmail("example@gmail.com");
        requestDto.setGender("MALE");
        requestDto.setDisplayMode("FULLNAME");
        Mockito.when(profileRepository.save(Mockito.any(Profile.class))).thenThrow(ConstraintViolationException.class);
        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(requestDto, OnCreate.class);
        Assertions.assertFalse(violations.isEmpty());
        Jwt mockedJwt = prepareJwt(this.testUserJwt,
                requestDto.getUserId(),
                requestDto.getUsername(),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getEmail());
        Mockito.when(decoder.decode(Mockito.any())).thenReturn(mockedJwt);
        Assertions.assertThrows(ConstraintViolationException.class, () -> profileManager.createProfile(this.testUserJwt));
    }

    @Test
    public void updateProfileFullSuccessTest(){
        ProfileDto requestDto = generateBaseProfileDto();
        Profile existingProfile = ProfileMapper.INSTANCE.toEntity(requestDto);
        existingProfile.setProfileId(1);

        Mockito.when(profileRepository.findProfileByProfileId(1)).thenReturn(Optional.of(existingProfile));

        // DTO with new data
        ProfileDto updateDto = new ProfileDto();
        updateDto.setFirstName("Billy");
        updateDto.setLastName("Herrington");
        updateDto.setBirthday(LocalDate.of(2004, 5, 21));
        updateDto.setEmail("gacchiemail@gmail.com");
        updateDto.setGender("FEMALE");
        updateDto.setDisplayMode("USERNAME");

        ProfileDto readyProfileDto = profileManager.updateProfile(updateDto, 1);

        updateDto.setProfileId(1);
        updateDto.setUserId("bfcd38f2-81ed-43be-94cf-9341ae60840c");
        updateDto.setUsername("testuser1");
        assertThat(readyProfileDto).usingRecursiveComparison().isEqualTo(updateDto);
    }

    @Test
    public void updateProfilePartiallySuccessTest(){
        ProfileDto requestDto = generateBaseProfileDto();
        Profile existingProfile = ProfileMapper.INSTANCE.toEntity(requestDto);

        Mockito.when(profileRepository.findProfileByProfileId(1)).thenReturn(Optional.of(existingProfile));

        // DTO with new data
        ProfileDto updateDto = new ProfileDto();
        updateDto.setFirstName("Billy");
        updateDto.setLastName(null);
        updateDto.setGender(null);
        updateDto.setDisplayMode("USERNAME");

        ProfileDto readyProfileDto = profileManager.updateProfile(updateDto, 1);

        requestDto.setFirstName("Billy");
        requestDto.setDisplayMode("USERNAME");

        assertThat(readyProfileDto).usingRecursiveComparison().isEqualTo(requestDto);
    }

    @Test
    public void updateProfileNotValidValuesTest(){
        ProfileDto requestDto = generateBaseProfileDto();
        Profile existingProfile = ProfileMapper.INSTANCE.toEntity(requestDto);

        Mockito.when(profileRepository.findProfileByProfileId(1)).thenReturn(Optional.of(existingProfile));

        // DTO with new data
        ProfileDto updateDto = new ProfileDto();
        updateDto.setDisplayMode("USERNAME");
        updateDto.setProfileId(10);
        updateDto.setUserId("UUID-4");

        Mockito.when(profileRepository.save(Mockito.any(Profile.class))).thenThrow(ConstraintViolationException.class);
        Set<ConstraintViolation<ProfileDto>> violations = validator.validate(requestDto, OnUpdate.class);
        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertThrows(ConstraintViolationException.class, () -> profileManager.updateProfile(updateDto, 1));
    }

    @Test
    public void updateProfileNotExistTest(){
        Mockito.when(profileRepository.findProfileByProfileId(1)).thenThrow(ProfileNotFound.class);
        ProfileDto profileDto = generateBaseProfileDto();
        Assertions.assertThrows(ProfileNotFound.class, () -> Assertions.assertNull(profileManager.updateProfile(profileDto, 1)));
    }

}
