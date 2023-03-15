package com.rcbg.afku.instodramat.unittests.photos;

import com.rcbg.afku.instodramat.photos.exceptions.ImageNotFound;
import com.rcbg.afku.instodramat.photos.services.ImageReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageReaderTest {

    private ImageReader imageReader;

    private final String pathToStorage = "src/test/resources/unittest_images";
    private final String imageName = "ok.PNG";

    @BeforeEach
    public void setup(){
        imageReader = new ImageReader();
        imageReader.setStorageLocalization(pathToStorage);
    }

    @Test
    public void getImageSuccess() throws IOException {
        byte[] image = imageReader.getImage(imageName);
        byte[] validImage = Files.readAllBytes(Paths.get(pathToStorage + "/" + imageName));
        Assertions.assertArrayEquals(validImage, image);
    }

    @Test
    public void getImageNotFound(){
        ImageNotFound ex = Assertions.assertThrows(ImageNotFound.class, () -> imageReader.getImage("randomImage.JPG"));
        Assertions.assertEquals("Path: " + pathToStorage + "/randomImage.JPG , Image not found", ex.getMessage());
    }
}
