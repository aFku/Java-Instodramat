package com.rcbg.afku.instodramat.integrationtests.authusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.authusers.dtos.FollowState;
import com.rcbg.afku.instodramat.authusers.dtos.FollowStateDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileMapper;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.integrationtests.TestContainersBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class ManageProfilesTest extends TestContainersBase {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProfileManager profileManager;

    @Autowired
    private ProfileRepository profileRepository;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    public ManageProfilesTest(){
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        profileRepository.deleteAll();
    }

    @Test
    public void testCreateProfileWithInvalidJwt() throws Exception {
        String token = "XXXX-XXXX-XXXX";

        MvcResult response = mockMvc.perform(post("/api/v1/profiles/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andReturn();
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }

    @Test
    public void testCreateProfileWithValidJwt() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        mockMvc.perform(post("/api/v1/profiles/create").
                header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.userId").value("d9f800a7-36f0-407d-bb65-2e62da1d8b4c"))
                .andExpect(jsonPath("$.data.username").value("bbrumhead0"))
                .andExpect(jsonPath("$.data.firstName").value("Brandy"))
                .andExpect(jsonPath("$.data.lastName").value("Brumhead"))
                .andExpect(jsonPath("$.data.email").value("bbrumhead0@cbslocal.com"))
                .andExpect(jsonPath("$.data.gender").value("NOT_DEFINED"))
                .andExpect(jsonPath("$.data.displayMode").value("USERNAME"));
    }

    @Test
    public void testCreateProfileAlreadyExists() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        mockMvc.perform(post("/api/v1/profiles/create")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt));

        mockMvc.perform(post("/api/v1/profiles/create").
                header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages[0]").value("Profile related to your userID already exists"));
    }

    @Test
    public void testUpdateProfileAllFields() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);
        int profileId = profile.getProfileId();


        ProfileDto updateDto = new ProfileDto();
        updateDto.setFirstName("newfirstname");
        updateDto.setLastName("newlastname");
        updateDto.setBirthday(LocalDate.of(1990, 5, 11));
        updateDto.setDisplayMode("FULLNAME");
        updateDto.setGender("FEMALE");
        updateDto.setEmail("newemail@example.com");

        String content = this.mapper.writeValueAsString(updateDto);
        mockMvc.perform(patch("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.firstName").value("newfirstname"))
                .andExpect(jsonPath("$.data.lastName").value("newlastname"))
                .andExpect(jsonPath("$.data.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.data.gender").value("FEMALE"))
                .andExpect(jsonPath("$.data.displayMode").value("FULLNAME"))
                .andExpect(jsonPath("$.data.birthday").value("1990-05-11"));
    }

    @Test
    public void testUpdateProfileNotAllFields() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);
        int profileId = profile.getProfileId();

        ProfileDto updateDto = new ProfileDto();
        updateDto.setUsername(null);
        updateDto.setFirstName("Firstname");
        updateDto.setBirthday(LocalDate.of(1990, 5, 11));


        String content = this.mapper.writeValueAsString(updateDto);
        mockMvc.perform(patch("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.username").value("bbrumhead0"))
                .andExpect(jsonPath("$.data.firstName").value("Firstname"))
                .andExpect(jsonPath("$.data.lastName").value("Brumhead"))
                .andExpect(jsonPath("$.data.email").value("bbrumhead0@cbslocal.com"))
                .andExpect(jsonPath("$.data.gender").value("NOT_DEFINED"))
                .andExpect(jsonPath("$.data.displayMode").value("USERNAME"))
                .andExpect(jsonPath("$.data.birthday").value("1990-05-11"));
    }

    @Test
    public void testUpdateProfileInvalidValues() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);
        int profileId = profile.getProfileId();

        ProfileDto updateDto = new ProfileDto();
        // Forbidden updates
        updateDto.setProfileId(5);
        updateDto.setUserId("XXX-XXX-XXX");
        updateDto.setUsername("x".repeat(257));

        // Allowed updates
        updateDto.setFirstName("y".repeat(260));
        updateDto.setLastName("z".repeat(270));
        updateDto.setBirthday(LocalDate.of(2077, 1, 15));
        updateDto.setEmail("example@example@.com");
        updateDto.setGender("MALEFE");
        updateDto.setDisplayMode("USERFULLNAME");

        String content = this.mapper.writeValueAsString(updateDto);

        String[] expectedMessages = {
                "profileId : You cannot update profileId",
                "userId : You cannot update userId",
                "username : You cannot update username",
                "firstName : Max size for this field is 255",
                "lastName : Max size for this field is 255",
                "birthday : Date must be from past",
                "email : Provide valid email",
                "gender : This field can contain only values: [MALE, FEMALE, NOT_DEFINED]",
                "displayMode : This field can contain only values: [FULLNAME, USERNAME]"
        };

        mockMvc.perform(patch("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt).content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages.length()").value(expectedMessages.length))
                .andExpect(jsonPath("$.messages", Matchers.containsInAnyOrder(expectedMessages)));
    }

    @Test
    public void testDeleteProfile() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);
        int profileId = profile.getProfileId();

        mockMvc.perform(delete("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        Assertions.assertFalse(profileRepository.existsByUserId(profile.getUserId()));
    }

    @Test
    public void testManageProfileWithoutOwnership() throws Exception{
        // Delete
        String jwt1 = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto1 = profileManager.jwtToProfileDto("Bearer " + jwt1);
        Profile profile1 = ProfileMapper.INSTANCE.toEntity(createDto1);
        profileRepository.save(profile1);
        int profileId = profile1.getProfileId();
        Assertions.assertTrue(profileRepository.existsByUserId(profile1.getUserId()));

        String jwt2 = obtainJwtTokenResponse("landreassen8", "s3cr3t");
        ProfileDto createDto2 = profileManager.jwtToProfileDto("Bearer " + jwt2);
        Profile profile2 = ProfileMapper.INSTANCE.toEntity(createDto2);
        profileRepository.save(profile2);

        mockMvc.perform(delete("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt2))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        Assertions.assertTrue(profileRepository.existsByUserId(profile1.getUserId()));

        // Update
        ProfileDto updateDto = new ProfileDto();
        String content = this.mapper.writeValueAsString(updateDto);
        mockMvc.perform(patch("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt2).content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testFollowSomeonesProfile() throws Exception{
        String jwt1 = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto1 = profileManager.jwtToProfileDto("Bearer " + jwt1);
        Profile profile1 = ProfileMapper.INSTANCE.toEntity(createDto1);
        profileRepository.save(profile1);

        String jwt2 = obtainJwtTokenResponse("landreassen8", "s3cr3t");
        ProfileDto createDto2 = profileManager.jwtToProfileDto("Bearer " + jwt2);
        Profile profile2 = ProfileMapper.INSTANCE.toEntity(createDto2);
        profileRepository.save(profile2);
        int profileId = profile2.getProfileId();

        String content = this.mapper.writeValueAsString(new FollowStateDto(FollowState.FOLLOW.toString()));
        mockMvc.perform(post("/api/v1/profiles/" + profileId + "/follows").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt1)
                .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        Page<ProfileDto> followers = profileManager.getPageOfProfilesThatAreFollowersOfGivenProfile(profileId, null);
        Assertions.assertFalse(followers.isEmpty());
        Assertions.assertTrue(followers.getContent().contains(ProfileMapper.INSTANCE.toDto(profile1)));
    }

    @Test
    public void testFollowProfileThatUserAlreadyFollows() throws Exception{
        String jwt1 = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto1 = profileManager.jwtToProfileDto("Bearer " + jwt1);
        Profile profile1 = ProfileMapper.INSTANCE.toEntity(createDto1);
        profileRepository.save(profile1);

        String jwt2 = obtainJwtTokenResponse("landreassen8", "s3cr3t");
        ProfileDto createDto2 = profileManager.jwtToProfileDto("Bearer " + jwt2);
        Profile profile2 = ProfileMapper.INSTANCE.toEntity(createDto2);
        profileRepository.save(profile2);
        int profileId = profile2.getProfileId();

        String content = this.mapper.writeValueAsString(new FollowStateDto(FollowState.FOLLOW.toString()));
        mockMvc.perform(post("/api/v1/profiles/" + profileId + "/follows").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt1)
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        mockMvc.perform(post("/api/v1/profiles/" + profileId + "/follows").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt1)
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages[0]").value("profileId: " + profile1.getProfileId() + " already follow profileId: " + profile2.getProfileId()));
    }

    @Test
    public void testUnfollowSomeonesProfile() throws Exception{
        String jwt1 = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto1 = profileManager.jwtToProfileDto("Bearer " + jwt1);
        Profile profile1 = ProfileMapper.INSTANCE.toEntity(createDto1);
        profileRepository.save(profile1);

        String jwt2 = obtainJwtTokenResponse("landreassen8", "s3cr3t");
        ProfileDto createDto2 = profileManager.jwtToProfileDto("Bearer " + jwt2);
        Profile profile2 = ProfileMapper.INSTANCE.toEntity(createDto2);
        profileRepository.save(profile2);
        int profileId = profile2.getProfileId();
        profileManager.setFollowProfileState(profile1.getUserId(), profile2.getProfileId(), new FollowStateDto(FollowState.FOLLOW.toString()));

        String content = this.mapper.writeValueAsString(new FollowStateDto(FollowState.UNFOLLOW.toString()));
        mockMvc.perform(post("/api/v1/profiles/" + profileId + "/follows").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt1)
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));

        Page<ProfileDto> followers = profileManager.getPageOfProfilesThatAreFollowersOfGivenProfile(profileId, null);
        Assertions.assertTrue(followers.isEmpty());
        Assertions.assertFalse(followers.getContent().contains(ProfileMapper.INSTANCE.toDto(profile1)));
    }

    @Test
    public void testUnfollowProfileThatUserNotFollow() throws Exception{
        String jwt1 = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto1 = profileManager.jwtToProfileDto("Bearer " + jwt1);
        Profile profile1 = ProfileMapper.INSTANCE.toEntity(createDto1);
        profileRepository.save(profile1);

        String jwt2 = obtainJwtTokenResponse("landreassen8", "s3cr3t");
        ProfileDto createDto2 = profileManager.jwtToProfileDto("Bearer " + jwt2);
        Profile profile2 = ProfileMapper.INSTANCE.toEntity(createDto2);
        profileRepository.save(profile2);
        int profileId = profile2.getProfileId();

        String content = this.mapper.writeValueAsString(new FollowStateDto(FollowState.UNFOLLOW.toString()));
        mockMvc.perform(post("/api/v1/profiles/" + profileId + "/follows").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt1)
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages[0]").value("profileId: " + profile1.getProfileId() + " do not follow profileId: " + profile2.getProfileId()));
    }

    @Test
    public void testSelfFollow() throws Exception{
        String jwt1 = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto1 = profileManager.jwtToProfileDto("Bearer " + jwt1);
        Profile profile1 = ProfileMapper.INSTANCE.toEntity(createDto1);
        profileRepository.save(profile1);
        int profileId = profile1.getProfileId();

        String content = this.mapper.writeValueAsString(new FollowStateDto(FollowState.FOLLOW.toString()));
        mockMvc.perform(post("/api/v1/profiles/" + profileId + "/follows").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt1)
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages[0]").value("You cannot follow or unfollow yourself"));
    }

}
