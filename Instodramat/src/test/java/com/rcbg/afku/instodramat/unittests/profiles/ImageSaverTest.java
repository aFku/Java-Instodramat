package com.rcbg.afku.instodramat.unittests.profiles;

import com.rcbg.afku.instodramat.photos.exceptions.ImageUploadException;
import com.rcbg.afku.instodramat.photos.services.ImageSaver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
public class ImageSaverTest {

    private static final Map<String, MockMultipartFile> multipartFileMap = new HashMap<>();

    @InjectMocks
    private ImageSaver imageSaver;

    @BeforeAll
    public static void setup() throws IOException {
        String pathToImages = "src/test/resources/unittest_images/";
        String[][] imageList = {
                {"ok", "ok.PNG"},
                {"largeSize", "largeSize.PNG"},
                {"wrongExtension", "wrongExtension.txt"},
                {"badRatio", "badRatio.PNG"},
                {"largeDimensions", "largeDimensions.PNG"}
        };
        for(String[] element: imageList){
            Path path = Paths.get(pathToImages + element[1]);
            String[] fileData = element[1].split("\\."); // Extension and OriginalName
            MockMultipartFile file = new MockMultipartFile(element[1], fileData[0], fileData[1], Files.readAllBytes(path));
            multipartFileMap.put(element[0], file);
        }
    }

    @BeforeEach
    public void setupEach(){
        imageSaver.setExtensions(new ArrayList<>(Arrays.stream("PNG,JPG,JPEG".split(",")).toList()));
        imageSaver.setExpectedRatio(1);
        imageSaver.setMaxSize(1048576);
        imageSaver.setMaxDimensions(2048);
        imageSaver.setStorageLocalization("storage/");
        imageSaver.setMaxSizeMessageInMb(10);
    }

    @Test
    public void testImageSaverValidateOkFile(){
        MockMultipartFile file = multipartFileMap.get("ok");
        imageSaver.validateMultipartImage(file);
        // Should nothing happen
    }

    @Test
    public void testImageSaverValidateTooLargeSize(){
        MockMultipartFile file = multipartFileMap.get("largeSize");
        ImageUploadException ex = Assertions.assertThrows(ImageUploadException.class, () -> imageSaver.validateMultipartImage(file));
        assertEquals(ex.getMessage(), "File is too large. Max file size is: 10 MB");
    }

    @Test
    public void testImageSaverValidateWrongExtension(){
        MockMultipartFile file = multipartFileMap.get("wrongExtension");
        ImageUploadException ex = Assertions.assertThrows(ImageUploadException.class, () -> imageSaver.validateMultipartImage(file));
        assertEquals(ex.getMessage(), "Invalid image extension: txt . Allowed only: [PNG, JPG, JPEG]");
    }

    @Test
    public void testImageSaverValidateBadRatio(){
        MockMultipartFile file = multipartFileMap.get("badRatio");
        ImageUploadException ex = Assertions.assertThrows(ImageUploadException.class, () -> imageSaver.validateMultipartImage(file));
        assertEquals(ex.getMessage(), "Cannot save image with ratio: 0.9717391 because expected is: 1");
    }

    @Test
    public void testImageSaverValidateTooLargeDimensions(){
        MockMultipartFile file = multipartFileMap.get("largeDimensions");
        ImageUploadException ex = Assertions.assertThrows(ImageUploadException.class, () -> imageSaver.validateMultipartImage(file));
        assertEquals(ex.getMessage(), "Cannot save image with pixel size: 2235 because max is: 2048");
    }

    @Test
    public void testImageSaverSaveOkImage() throws IOException {
        MockMultipartFile file = multipartFileMap.get("ok");
        String name = "test_name.JPEG";

        String savedName = imageSaver.saveMultipartFile(file, name);
        assertEquals(savedName, "storage\\" + name);
        Files.delete(Paths.get(savedName));
    }

    @Test
    public void testImageSaverGenerateName(){
        String userId = "af6789a4-5515-45c9-9329-3c748f2799cb";
        LocalDate date = LocalDate.of(1998, 5, 2);
        int photoId = 23;

        String generatedName = imageSaver.generateBase64Name(userId, date, photoId);
        String expectedName = "YWY2Nzg5YTQtNTUxNS00NWM5LTkzMjktM2M3NDhmMjc5OWNiMTk5OC0wNS0wMjIz";
        Assertions.assertEquals(generatedName, expectedName);
    }
}
