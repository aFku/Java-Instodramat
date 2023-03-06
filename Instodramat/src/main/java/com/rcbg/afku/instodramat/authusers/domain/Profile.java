package com.rcbg.afku.instodramat.authusers.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer profileId;

    @Column(length = 200)
    String userId;
    String username;
    String firstName;
    String lastName;
    LocalDate birthday;
    String email;
    Gender gender;
    DisplayMode displayMode;

    @ManyToMany
    @JoinTable(
            name = "profile_follows_relationship",
            joinColumns = @JoinColumn(name = "profileId"),
            inverseJoinColumns = @JoinColumn(name = "follower_person_id")
    )
    @JsonIgnoreProperties
    Set<Profile> followers;

    @ManyToMany(mappedBy = "likes")
    Set<Photo> likes = new HashSet<>();

    public void addToFollowers(Profile profile){
        followers.add(profile);
    }

    public void removeFromFollowers(Profile profile){
        followers.remove(profile);
    }
}
