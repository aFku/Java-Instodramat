package com.rcbg.afku.instodramat.unittests.photos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.dtos.FollowStateDto;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import com.rcbg.afku.instodramat.photos.domain.Comment;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.dtos.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DTOsValidationTest {

    private static MultipartFile image;
    private static Validator validator;

    @BeforeAll
    public static void setup() throws IOException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        String pathToImage = "src/test/resources/unittest_images/ok.PNG";
        Path path = Paths.get(pathToImage);
        image = new MockMultipartFile("ok.png", "ok", "png", Files.readAllBytes(path));
    }

    @Test
    public void photoRequestDtoOnCreateTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setFile(image);

        Set<ConstraintViolation<PhotoRequestDto>> violations = validator.validate(dto, OnCreate.class);
        Assertions.assertTrue(violations.isEmpty());


        dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setFile(null);

        violations = validator.validate(dto, OnCreate.class);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("image : image cannot be null", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void photoRequestDtoOnUpdateTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setFile(null);

        Set<ConstraintViolation<PhotoRequestDto>> violations = validator.validate(dto, OnUpdate.class);
        Assertions.assertTrue(violations.isEmpty());


        dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setFile(image);

        violations = validator.validate(dto, OnUpdate.class);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("image : you cannot update existing photo file", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void photoRequestDtoCommonTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("x".repeat(256));
        dto.setFile(image);

        Set<ConstraintViolation<PhotoRequestDto>> violations = validator.validate(dto);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("description : max size for description is 255 characters", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void photoMapperRequestDtoToEntityTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setFile(image);
        LocalDateTime dateTime = LocalDateTime.now();
        String path = "random/path/to/file.jpg";
        Profile author = new Profile();
        author.setProfileId(15);


        Photo photo = PhotoMapper.INSTANCE.requestDtoToEntity(dto, dateTime, path, author);

        Assertions.assertEquals("Description", photo.getDescription());
        Assertions.assertEquals(dateTime, photo.getPublishDate());
        Assertions.assertEquals(path, photo.getPathToFile());
        Assertions.assertEquals(author.getUsername(), photo.getAuthor().getUsername());
    }

    @Test
    public void photoMapperEntityToResponseDtoTest(){
        Photo photo = new Photo();
        photo.setPhotoId(1);
        photo.setDescription("Description");
        photo.setPathToFile("random/path/to/file.jpg");
        Profile author = new Profile();
        author.setProfileId(1);
        photo.setAuthor(author);
        LocalDateTime dateTime = LocalDateTime.now();
        photo.setPublishDate(dateTime);

        PhotoResponseDto responseDto = PhotoMapper.INSTANCE.EntityToResponseDto(photo);

        Assertions.assertEquals(1, responseDto.getPhotoId());
        Assertions.assertEquals("Description", responseDto.getDescription());
        Assertions.assertEquals(dateTime, responseDto.getPublishDate());
        Assertions.assertEquals(author.getProfileId(), responseDto.getAuthorId());
        Assertions.assertEquals("random/path/to/file.jpg", responseDto.getImage());
    }

    @Test
    public void photoMapperUpdateEntityWithRequestDtoTest(){
        Photo photo = new Photo();
        photo.setPhotoId(1);
        photo.setDescription("Description");
        photo.setPathToFile("random/path/to/file.jpg");
        Profile author = new Profile();
        author.setProfileId(1);
        photo.setAuthor(author);
        LocalDateTime dateTime = LocalDateTime.now();
        photo.setPublishDate(dateTime);

        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setFile(null);
        dto.setDescription("NewDescription");
        Photo updatedPhoto = PhotoMapper.INSTANCE.updateEntityWithRequestDto(dto, photo);

        Assertions.assertEquals(dto.getDescription(), updatedPhoto.getDescription());
        Assertions.assertEquals(photo.getPathToFile(), updatedPhoto.getPathToFile());
    }

    @Test
    public void likeStatusDtoTest(){
        LikeDto likeDto = new LikeDto();
        likeDto.setState("LIKEDISLIKE");
        Set<ConstraintViolation<LikeDto>> violations = validator.validate(likeDto);
        Assertions.assertEquals(1, violations.size());
        String message = violations.stream().map(ConstraintViolation::getMessage).toList().get(0);
        Assertions.assertEquals("state : This field can contain only values: [LIKE, DISLIKE]", message);

        likeDto.setState(null);
        violations = validator.validate(likeDto);
        Assertions.assertEquals(1, violations.size());
        message = violations.stream().map(ConstraintViolation::getMessage).toList().get(0);
        Assertions.assertEquals("state : This field cannot be empty", message);

        likeDto.setState("LIKE");
        violations = validator.validate(likeDto);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void testCommentRequestDtoOk(){
        CommentRequestDto dto = new CommentRequestDto();
        dto.setContent("Regular content");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    public void testCommentRequestDtoFail(){
        CommentRequestDto dto = new CommentRequestDto();
        dto.setContent("x".repeat(256));

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("content : Max size for this field is 255", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));

        dto.setContent(" ");
        violations = validator.validate(dto);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("content : This field cannot be empty", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));

        dto.setContent(null);
        violations = validator.validate(dto);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("content : This field cannot be empty", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void testCommentMapperRequestDtoToEntity(){
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("Typical content");
        LocalDateTime date = LocalDateTime.now();
        Photo photo = new Photo();
        photo.setPhotoId(5);
        Profile profile = new Profile();
        profile.setProfileId(54);

        Comment comment = CommentMapper.INSTANCE.requestDtoToEntity(requestDto, date, profile, photo);
        Assertions.assertEquals(requestDto.getContent(), comment.getContent());
        Assertions.assertEquals(date, comment.getPublishDate());
        Assertions.assertEquals(profile.getProfileId(), comment.getAuthor().getProfileId());
        Assertions.assertEquals(photo.getPhotoId(), comment.getPhoto().getPhotoId());
    }

    @Test
    public void testCommentMapperEntityToResponseDto(){
        Comment comment = new Comment();
        comment.setCommentId(32);
        comment.setContent("Content/txt");
        LocalDateTime date = LocalDateTime.now();
        comment.setPublishDate(date);
        Photo photo = new Photo();
        photo.setPhotoId(5);
        comment.setPhoto(photo);
        Profile profile = new Profile();
        profile.setProfileId(54);
        comment.setAuthor(profile);


        CommentResponseDto responseDto = CommentMapper.INSTANCE.EntityToResponseDto(comment);
        Assertions.assertEquals(comment.getContent(), responseDto.getContent());
        Assertions.assertEquals(comment.getCommentId(), responseDto.getCommentId());
        Assertions.assertEquals(comment.getPublishDate(), responseDto.getPublishDate());
        Assertions.assertEquals(comment.getAuthor().getProfileId(), responseDto.getAuthorId());
        Assertions.assertEquals(comment.getPhoto().getPhotoId(), responseDto.getPhotoId());
    }
}
