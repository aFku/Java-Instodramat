package com.rcbg.afku.instodramat.integrationtests.photos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileDto;
import com.rcbg.afku.instodramat.authusers.dtos.ProfileMapper;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.integrationtests.TestContainersBase;
import com.rcbg.afku.instodramat.photos.domain.Comment;
import com.rcbg.afku.instodramat.photos.domain.CommentRepository;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.CommentMapper;
import com.rcbg.afku.instodramat.photos.dtos.CommentRequestDto;
import com.rcbg.afku.instodramat.photos.services.CommentManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class ManageCommentsTest extends TestContainersBase {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProfileManager profileManager;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private CommentManager commentManager;

    @Autowired
    private CommentRepository commentRepository;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        profileRepository.deleteAll();
        photoRepository.deleteAll();
        mapper = new ObjectMapper();
    }

    public MockMultipartFile prepareMultipartFile(String imageName) throws IOException {
        String pathToImages = "src/test/resources/unittest_images/" + imageName;
        Path path = Paths.get(pathToImages);
        return new MockMultipartFile("file", imageName, "PNG", Files.readAllBytes(path));
    }

    public int[] createProfileWithImage(String username){
        String jwt = obtainJwtTokenResponse(username, "s3cr3t");
        ProfileDto createDto = profileManager.jwtToProfileDto("Bearer " + jwt);
        Profile profile = ProfileMapper.INSTANCE.toEntity(createDto);
        profileRepository.save(profile);

        Photo photo = new Photo();
        photo.setDescription("Description");
        photo.setAuthor(profile);
        photo.setPathToFile("path/to/file.jpg");
        photo.setPublishDate(LocalDateTime.now());
        photoRepository.save(photo);

        return new int[]{profile.getProfileId(), photo.getPhotoId()};
    }


    @Test
    public void testGetCommentsForPhoto() throws Exception {
        int[] person1 = createProfileWithImage("bbrumhead0");
        int[] person2 = createProfileWithImage("nspeers7");

        LocalDateTime publishDate = LocalDateTime.now();
        Profile commentAuthor = profileManager.getDomainObjectByProfileId(person2[0]);
        Photo photo = photoRepository.findById(person1[1]).orElse(null);
        Assertions.assertNotNull(photo);
        CommentRequestDto requestDto = new CommentRequestDto("Hello!123");

        Comment comment = CommentMapper.INSTANCE.requestDtoToEntity(requestDto, publishDate, commentAuthor, photo);
        commentRepository.save(comment);

        requestDto.setContent("Lore");
        comment = CommentMapper.INSTANCE.requestDtoToEntity(requestDto, publishDate, commentAuthor, photo);
        commentRepository.save(comment);

        commentAuthor = profileManager.getDomainObjectByProfileId(person1[0]);
        requestDto.setContent("It's me");
        comment = CommentMapper.INSTANCE.requestDtoToEntity(requestDto, publishDate, commentAuthor, photo);
        commentRepository.save(comment);

        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        mockMvc.perform(get("/api/v1/photos/" + photo.getPhotoId() + "/comments").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.totalElements").value(3));
    }

    @Test
    public void testGetCommentsForPhotoNotFound() throws Exception {
        createProfileWithImage("bbrumhead0");
        String jwt = obtainJwtTokenResponse("bbrumhead0", "s3cr3t");
        mockMvc.perform(get("/api/v1/photos/523352/comments").header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }

    // Create comment for own photo
    // Create comment for not own photo
    // create comment with wrong request data

    // Delete comment with ownership
    // delete comment without ownership
    // Delete commentId not related to photo
}
