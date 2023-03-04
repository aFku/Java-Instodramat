package com.rcbg.afku.instodramat.unittests.photos;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.domain.PhotoRepository;
import com.rcbg.afku.instodramat.photos.dtos.PhotoRequestDto;
import com.rcbg.afku.instodramat.photos.dtos.PhotoResponseDto;
import com.rcbg.afku.instodramat.photos.exceptions.ImageUploadException;
import com.rcbg.afku.instodramat.photos.exceptions.SavePhotoException;
import com.rcbg.afku.instodramat.photos.services.ImageSaver;
import com.rcbg.afku.instodramat.photos.services.PhotoManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PhotoManagerTest {

    @Mock
    private ImageSaver imageSaver;

    @Mock
    private PhotoRepository repository;

    @Mock
    private ProfileManager profileManager;

    @InjectMocks
    private PhotoManager photoManager;

    private static MultipartFile image;

    @BeforeAll
    public static void setup() throws IOException {
        String pathToImage = "src/test/resources/unittest_images/ok.PNG";
        Path path = Paths.get(pathToImage);
        image = new MockMultipartFile("ok.png", "ok", "png", Files.readAllBytes(path));
    }

    @Test
    public void failPostCreatingWhenImageSaverFailTest() throws ImageUploadException {
        PhotoRequestDto dto = new PhotoRequestDto();
        dto.setFile(image);
        dto.setDescription("Description");
        Mockito.when(imageSaver.saveMultipartFile(Mockito.any(), Mockito.any())).thenThrow(new ImageUploadException("Error"));
        Assertions.assertThrows(SavePhotoException.class, () -> photoManager.createPhotoPost(dto, "userId"));
    }

    @Test
    public void postCreatingSuccessTest() throws ImageUploadException {
        String authorId = "authorId";
        Profile profile = new Profile();
        profile.setProfileId(64);
        profile.setUserId(authorId);

        PhotoRequestDto requestDto = new PhotoRequestDto();
        requestDto.setFile(image);
        requestDto.setDescription("Description");

        Mockito.when(profileManager.getDomainObjectByUserId(authorId)).thenReturn(profile);
        Mockito.when(imageSaver.saveMultipartFile(Mockito.any(), Mockito.any())).thenReturn("path/to/photo.jpg");
        Mockito.when(repository.save(Mockito.any())).thenReturn(null);

        PhotoResponseDto responseDto = photoManager.createPhotoPost(requestDto, authorId);
        Assertions.assertEquals("path/to/photo.jpg", responseDto.getImage());
        Assertions.assertEquals("Description", responseDto.getDescription());
        Assertions.assertEquals(64, responseDto.getAuthorId());
    }

    @Test
    public void postUpdateSuccessTest(){
        Profile profile = new Profile();
        profile.setProfileId(32);

        Photo photo = new Photo();
        photo.setPhotoId(5);
        photo.setPathToFile("path/to/file.png");
        photo.setAuthor(profile);
        photo.setPublishDate(LocalDateTime.now());
        photo.setDescription("Description");

        PhotoRequestDto requestDto = new PhotoRequestDto();
        requestDto.setFile(null);
        requestDto.setDescription("updateDescription123");

        Mockito.when(repository.findById(photo.getPhotoId())).thenReturn(Optional.of(photo));
        PhotoResponseDto updatedPhoto = photoManager.updatePhoto(requestDto, photo.getPhotoId());

        Assertions.assertEquals(updatedPhoto.getPhotoId(), photo.getPhotoId());
        Assertions.assertEquals(photo.getPathToFile(), updatedPhoto.getImage());
        Assertions.assertEquals(requestDto.getDescription(), updatedPhoto.getDescription());
        Assertions.assertEquals(photo.getAuthor().getProfileId(), profile.getProfileId());
    }
}
