package com.rcbg.afku.instodramat.photos.services;

import com.rcbg.afku.instodramat.photos.exceptions.ImageNotFound;
import com.rcbg.afku.instodramat.photos.exceptions.ImageReadException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Getter
@Setter
public class ImageReader {

    @Value("${photos.storage.path}")
    String storageLocalization;

    public byte[] getImage(String imageName){
        String filePath = storageLocalization + '/' + imageName;
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            throw new ImageNotFound("Path: " + filePath + " , Image not found");
        }

        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e){
            throw new ImageReadException("Path: " + filePath + " , cannot load file due to: " + e.getMessage());
        }
    }
}
