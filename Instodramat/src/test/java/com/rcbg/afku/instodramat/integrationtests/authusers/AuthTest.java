package com.rcbg.afku.instodramat.integrationtests.authusers;

import com.rcbg.afku.instodramat.authusers.domain.DisplayMode;
import com.rcbg.afku.instodramat.authusers.domain.Gender;
import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.integrationtests.TestContainersBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class AuthTest extends TestContainersBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProfileRepository profileRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        profileRepository.deleteAll();
    }

    @Test
    public void testAccessResourceWithValidJwtAndProfile() throws Exception {
        Profile profile = new Profile();
        profile.setUserId("d9f800a7-36f0-407d-bb65-2e62da1d8b4c");
        profile.setUsername("bbrumhead0");
        profile.setEmail("bbrumhead0@cbslocal.com");
        profile.setFirstName("Brandy");
        profile.setLastName("Brumhead");
        profile.setBirthday(LocalDate.of(1999, 5, 12));
        profile.setDisplayMode(DisplayMode.FULLNAME);
        profile.setGender(Gender.MALE);
        profileRepository.save(profile);
        int id = profile.getProfileId();

        String token = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");

        MvcResult response = mockMvc.perform(get("/api/v1/profiles/" + id).header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andReturn();
        Assertions.assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
    }

    @Test
    public void testAccessResourceWithInvalidJwt() throws Exception {
        String token = "XXX-XXXX-XXX";

        MvcResult response = mockMvc.perform(get("/api/v1/profiles/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andReturn();
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }

    @Test
    public void testAccessResourceWithValidJwtWithoutProfile() throws Exception{
        String token = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");

        MvcResult response = mockMvc.perform(get("/api/v1/profiles/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(jsonPath("$.messages[0]").value("You do not have a profile. Please sign up with registration endpoint.")).andReturn();
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }


}
