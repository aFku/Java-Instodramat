package com.rcbg.afku.instodramat.photos.controllers;

import com.rcbg.afku.instodramat.photos.services.ImageReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

    ImageReader imageReader;

    @Autowired
    public ImageController(ImageReader imageReader) {
        this.imageReader = imageReader;
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable("imageName") String imageName){
        byte[] loadedImage = imageReader.getImage(imageName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(loadedImage);
    }
}
