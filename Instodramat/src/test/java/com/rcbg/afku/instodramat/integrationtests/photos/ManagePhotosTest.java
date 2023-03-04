package com.rcbg.afku.instodramat.integrationtests.photos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileMapper;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.integrationtests.TestContainersBase;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.PhotoRequestDto;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class ManagePhotosTest extends TestContainersBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProfileManager profileManager;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PhotoRepository photoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        profileRepository.deleteAll();
        photoRepository.deleteAll();
    }

    public MockMultipartFile prepareMultipartFile(String imageName) throws IOException {
        String pathToImages = "src/test/resources/unittest_images/" + imageName;
        Path path = Paths.get(pathToImages);
        return new MockMultipartFile("file", imageName, "PNG", Files.readAllBytes(path));
    }

    @Test
    public void testCreatePhotoPostWithOkImage() throws Exception {
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);

        mockMvc.perform(multipart("/api/v1/photos").file(prepareMultipartFile("OK.png")).param("description", "testDescription").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.description").value("testDescription"))
                .andExpect(jsonPath("$.data.authorId").value(profile.getProfileId()));
    }

    @Test
    public void testCreatePhotoPostWithBadImage() throws Exception {
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);

        mockMvc.perform(multipart("/api/v1/photos").file(prepareMultipartFile("badRatio.png")).param("description", "testDescription").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Cannot save image with ratio: 0.9717391 because expected is: 1"));
    }

    @Test
    public void testUpdatePhotoOk() throws Exception {
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);

        Photo photo = new Photo();
        photo.setDescription("Description");
        photo.setAuthor(profile);
        photo.setPathToFile("path/to/file.jpg");
        photo.setPublishDate(LocalDateTime.now());
        photoRepository.save(photo);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/photos/" + photo.getPhotoId()).param("description", "updated").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("updated"));
    }

    @Test
    public void testUpdatePhotoNotOwned() throws Exception {
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        String jwtNotOwned = obtainJwtTokenResponse("nspeers7", "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        ProfileDto createDtoNotOwned = profileManager.jwtToProfileDto("Bearer " + jwtNotOwned);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        Profile profileNotOwned = ProfileMapper.INSTANCE.toEntity(createDtoNotOwned);
        profileRepository.save(profile);
        profileRepository.save(profileNotOwned);

        Photo photo = new Photo();
        photo.setDescription("Description");
        photo.setAuthor(profile);
        photo.setPathToFile("path/to/file.jpg");
        photo.setPublishDate(LocalDateTime.now());
        photoRepository.save(photo);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/photos/" + photo.getPhotoId()).param("description", "updated").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtNotOwned))
                .andExpect(status().isForbidden());
    }


}
