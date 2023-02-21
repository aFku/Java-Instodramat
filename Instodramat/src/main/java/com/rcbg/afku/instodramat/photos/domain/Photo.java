package com.rcbg.afku.instodramat.photos.domain;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "likes")
    Set<Profile> likes;

}
