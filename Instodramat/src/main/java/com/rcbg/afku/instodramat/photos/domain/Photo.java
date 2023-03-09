package com.rcbg.afku.instodramat.photos.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rcbg.afku.instodramat.authusers.domain.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer photoId;

    LocalDateTime publishDate;
    String description;
    String pathToFile;

    @ManyToOne
    Profile author;

    @JsonIgnoreProperties
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Profile> likes = new HashSet<>();;

    public void addLike(Profile profile){
        likes.add(profile);
    }

    public void removeLike(Profile profile){
        likes.remove(profile);
    }

    @PostRemove
    public void deleteRelatedFile() throws IOException {
        Path path = Paths.get(this.pathToFile);
        if(Files.exists(path)){
            Files.delete(path);
        }
    }

}
