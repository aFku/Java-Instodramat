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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
                {"largeSize", "largeSize.PNG"}
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
    public void testImageSaverValidateToLargeSize(){
        MockMultipartFile file = multipartFileMap.get("largeSize");
        Assertions.assertThrows(ImageUploadException.class, () -> imageSaver.validateMultipartImage(file), "File is too large. Max file size is: 10 MB");
    }

    // Add more tests
}
