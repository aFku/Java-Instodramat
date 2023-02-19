package com.rcbg.afku.instodramat.photos.services;

import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.photos.exceptions.ImageUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

public class ImageSaver {

    private final Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    @Value("${photos.storage.path}")
    String storageLocalization;

    @Value("#{'${photos.available-extensions}'.split(',')}")
    List<String> extensions;

    @Value("${photos.max-size.real-value}")
    long maxSize;

    @Value("${photos.max-size.human-readable-mb}")
    int maxSizeMessageInMb;

    @Value("${photos.dimensions.max}")
    int maxDimensions;

    @Value("${photos.dimensions.expected-ratio-to-one}")
    int expectedRatio;


    public String generateBase64Name(String userId, LocalDate date, int photoId){
        String stringToEncode = userId + date.toString() + photoId;
        return Base64.getEncoder().encodeToString(stringToEncode.getBytes());
    }

    private InputStream getInputStreamFromMultipart(MultipartFile image){
        try{
            return image.getInputStream();
        } catch (IOException ex){
            logger.error("Failed on getting input stream: " + ex.getMessage());
            throw new ImageUploadException("Cannot save image due to corrupted data");
        }
    }

    private void validateExtension(MultipartFile image){
        String imageExtension = image.getContentType();
        if(imageExtension == null || ! extensions.contains(imageExtension.toUpperCase())) {
            throw new ImageUploadException("Invalid image extension: " + imageExtension + " . Allowed only: " + extensions);
        }
    }

    private void validateSize(MultipartFile image){
        long imageSize = image.getSize();
        if(imageSize > maxSize) {
            throw new ImageUploadException("File is too large. Max file size is: " + maxSizeMessageInMb + " MB");
        }
    }

    private void validateDimensions(MultipartFile image){
        float height, width;

        try {
            BufferedImage buffer = ImageIO.read(getInputStreamFromMultipart(image));
            height = buffer.getHeight();
            width = buffer.getWidth();
        } catch (IOException ex){
            logger.error("Failed on validating file's dimensions: " + ex.getMessage());
            throw new ImageUploadException("Cannot save image due to corrupted data");
        }

        if( height == 0 || width == 0 ){
            logger.error("Failed on validating file's dimensions. Zero value dimension: Height: " + height + " Width: " + width);
            throw new ImageUploadException("Cannot save image with 0 as dimension value");
        }

        float ratio = height / width;
        if( ratio != (float) expectedRatio ){
            logger.error("Failed on validating file's dimensions. Bad ratio: " + ratio);
            throw new ImageUploadException("Cannot save image with ratio: " + ratio + " because expected is: " + expectedRatio);
        }

        if( height > maxDimensions ){
            logger.error("Failed on validating file's dimensions. Too large pixel size: " + (int) height);
            throw new ImageUploadException("Cannot save image with pixel size: " + (int) height + " because max is: " + maxDimensions);
        }
    }

    public void validateMultipartImage(MultipartFile image){
        validateSize(image);
        validateExtension(image);
        validateDimensions(image);
    }

    public String saveMultipartFile(MultipartFile image, String name){
        logger.info("Starting saving file: " + image.getName() + " as file: " + name);
        validateMultipartImage(image);
        Path storagePath = Paths.get(storageLocalization);
        InputStream inputStream = getInputStreamFromMultipart(image);
        Path filePath = storagePath.resolve(name);
        try{
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File: " + name + " saved!");
        } catch (IOException ex){
            logger.error("Failed on saving file to storage: " + ex.getMessage());
            throw new ImageUploadException("Cannot save image due to corrupted data");
        }
        return filePath.toString();
    }

    public String getStorageLocalization() {
        return storageLocalization;
    }

    public void setStorageLocalization(String storageLocalization) {
        this.storageLocalization = storageLocalization;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSizeMessageInMb() {
        return maxSizeMessageInMb;
    }

    public void setMaxSizeMessageInMb(int maxSizeMessageInMb) {
        this.maxSizeMessageInMb = maxSizeMessageInMb;
    }

    public int getMaxDimensions() {
        return maxDimensions;
    }

    public void setMaxDimensions(int maxDimensions) {
        this.maxDimensions = maxDimensions;
    }

    public int getExpectedRatio() {
        return expectedRatio;
    }

    public void setExpectedRatio(int expectedRatio) {
        this.expectedRatio = expectedRatio;
    }
}
