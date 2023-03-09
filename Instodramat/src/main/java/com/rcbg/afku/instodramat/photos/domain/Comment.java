package com.rcbg.afku.instodramat.photos.domain;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentId;

    String content;
    LocalDateTime publishDate;

    @ManyToOne(cascade = CascadeType.REMOVE)
    Profile author;

    @ManyToOne(cascade = CascadeType.REMOVE)
    Photo photo;
}
