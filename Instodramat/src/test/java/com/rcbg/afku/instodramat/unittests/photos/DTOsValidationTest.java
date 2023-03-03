package com.rcbg.afku.instodramat.unittests.photos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.common.validators.groups.OnCreate;
import com.rcbg.afku.instodramat.common.validators.groups.OnUpdate;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.dtos.PhotoMapper;
import com.rcbg.afku.instodramat.photos.dtos.PhotoRequestDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import com.rcbg.afku.instodramat.photos.services.ImageSaver;
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
        dto.setImage(image);

        Set<ConstraintViolation<PhotoRequestDto>> violations = validator.validate(dto, OnCreate.class);
        Assertions.assertTrue(violations.isEmpty());


        dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setImage(null);

        violations = validator.validate(dto, OnCreate.class);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("image : image cannot be null", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void photoRequestDtoOnUpdateTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setImage(null);

        Set<ConstraintViolation<PhotoRequestDto>> violations = validator.validate(dto, OnUpdate.class);
        Assertions.assertTrue(violations.isEmpty());


        dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setImage(image);

        violations = validator.validate(dto, OnUpdate.class);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("image : you cannot update existing photo file", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void photoRequestDtoCommonTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("x".repeat(256));
        dto.setImage(image);

        Set<ConstraintViolation<PhotoRequestDto>> violations = validator.validate(dto);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("description : max size for description is 255 characters", violations.stream().map(ConstraintViolation::getMessage).toList().get(0));
    }

    @Test
    public void photoMapperRequestDtoToEntityTest(){
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setDescription("Description");
        dto.setImage(image);
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
        dto.setImage(null);
        dto.setDescription("NewDescription");
        Photo updatedPhoto = PhotoMapper.INSTANCE.updateEntityWithRequestDto(dto);

        Assertions.assertEquals(dto.getDescription(), updatedPhoto.getDescription());
    }
}
