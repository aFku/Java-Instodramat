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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class FetchProfilesTest extends TestContainersBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProfileManager profileManager;

    @Autowired
    private ProfileRepository profileRepository;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    public FetchProfilesTest(){
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        profileRepository.deleteAll();
    }

    @Test
    public void getSingleProfile() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);
        int profileId = profile.getProfileId();
        mockMvc.perform(get("/api/v1/profiles/" + profileId).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.userId").value(createDto.getUserId()))
                .andExpect(jsonPath("$.data.profileId").value(profileId))
                .andExpect(jsonPath("$.data.username").value(createDto.getUsername()))
                .andExpect(jsonPath("$.data.firstName").value(createDto.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(createDto.getLastName()))
                .andExpect(jsonPath("$.data.email").value(createDto.getEmail()))
                .andExpect(jsonPath("$.data.gender").value(createDto.getGender()))
                .andExpect(jsonPath("$.data.displayMode").value(createDto.getDisplayMode()));
    }

    @Test
    public void getSingleProfileNotFound() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);
        mockMvc.perform(get("/api/v1/profiles/2137").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void getPageOfProfilesWithoutPagination() throws Exception{
        String[] users = {"bbrumhead0", "manstead1", "aledram2", "hpickup3", "rmelbourn4", "hcullity5", "rgerman6"};
        for(String user: users){
            String jwt = obtainJwtTokenResponse(user, "s3cr3t");
            ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
            Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
            profileRepository.save(profile);
        }
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");

        mockMvc.perform(get("/api/v1/profiles").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.length()").value(users.length));
    }

    @Test
    public void getPageOfProfilesWithCorrectPagination() throws Exception{
        String[] users = {"bbrumhead0", "manstead1", "aledram2", "hpickup3", "rmelbourn4", "hcullity5", "rgerman6"};
        for(String user: users){
            String jwt = obtainJwtTokenResponse(user, "s3cr3t");
            ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
            Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
            profileRepository.save(profile);
        }
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");

        String pageableStringFirst = "?page=0&size=5";
        mockMvc.perform(get("/api/v1/profiles" + pageableStringFirst).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.length()").value(5));

        String pageableStringLast = "?page=1&size=5";
        mockMvc.perform(get("/api/v1/profiles" + pageableStringLast).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    public void getPageOfProfilesWithWrongPaginationParameters() throws Exception{
        String[] users = {"bbrumhead0", "manstead1", "aledram2", "hpickup3", "rmelbourn4", "hcullity5", "rgerman6"};
        for(String user: users){
            String jwt = obtainJwtTokenResponse(user, "s3cr3t");
            ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
            Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
            profileRepository.save(profile);
        }
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        String pageableString;

        // PageSize - negative
        pageableString = "?page=0&size=-1";
        mockMvc.perform(get("/api/v1/profiles" + pageableString).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(20))
                .andExpect(jsonPath("$.data.length()").value(7));

        // PageSize - zero
        pageableString = "?page=1&size=0";
        mockMvc.perform(get("/api/v1/profiles" + pageableString).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(1))
                .andExpect(jsonPath("$.pagination.pageSize").value(20))
                .andExpect(jsonPath("$.data.length()").value(0));

        // PageSize - greater than max
        pageableString = "?page=0&size=1000";
        mockMvc.perform(get("/api/v1/profiles" + pageableString).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(50))
                .andExpect(jsonPath("$.data.length()").value(7));

        // PageNumber - negative
        pageableString = "?page=-1&size=5";
        mockMvc.perform(get("/api/v1/profiles" + pageableString).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(5))
                .andExpect(jsonPath("$.data.length()").value(5));

        // PageNumber - greater than last
        pageableString = "?page=10&size=5";
        mockMvc.perform(get("/api/v1/profiles" + pageableString).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(10))
                .andExpect(jsonPath("$.pagination.pageSize").value(5))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    public void testGetFollowersStats() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile collectorProfile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(collectorProfile);
        int fromProfileDirectionCheckId = 1;

        String[] users = {"manstead1", "aledram2", "hpickup3", "rmelbourn4", "hcullity5", "rgerman6"};
        for(String user: users){
            jwt = obtainJwtTokenResponse(user, "s3cr3t");
            createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
            Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
            profileRepository.save(profile);
            profileManager.setFollowProfileState(profile.getUserId(), collectorProfile.getProfileId(), new FollowStateDto(FollowState.FOLLOW.toString()));
            fromProfileDirectionCheckId = profile.getProfileId();
        }
        jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");

        String pageableString = "?page=0&size=50";
        mockMvc.perform(get("/api/v1/profiles/" + collectorProfile.getProfileId() + "/follows" + pageableString + "&direction=TO_PROFILE").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(50))
                .andExpect(jsonPath("$.data.length()").value(6));

        pageableString = "?page=0&size=50";
        mockMvc.perform(get("/api/v1/profiles/" + fromProfileDirectionCheckId + "/follows" + pageableString + "&direction=FROM_PROFILE").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(50))
                .andExpect(jsonPath("$.data.length()").value(1));

        pageableString = "?page=0&size=50";
        mockMvc.perform(get("/api/v1/profiles/" + collectorProfile.getProfileId() + "/follows" + pageableString + "&direction=FROM_PROFILE").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.pagination.pageNumber").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(50))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    public void testGetFollowersStatsBadDirection() throws Exception{
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile collectorProfile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(collectorProfile);

        String[] users = {"manstead1", "aledram2"};
        for(String user: users){
            jwt = obtainJwtTokenResponse(user, "s3cr3t");
            createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
            Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
            profileRepository.save(profile);
            profileManager.setFollowProfileState(profile.getUserId(), collectorProfile.getProfileId(), new FollowStateDto(FollowState.FOLLOW.toString()));
        }
        jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");

        String pageableString = "?page=0&size=50";
        mockMvc.perform(get("/api/v1/profiles/" + collectorProfile.getProfileId() + "/follows" + pageableString + "&direction=TOFROM_PROFILE").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.messages[0]").value("direction parameter accepts only values: [FROM_PROFILE, TO_PROFILE]"));
    }
}
