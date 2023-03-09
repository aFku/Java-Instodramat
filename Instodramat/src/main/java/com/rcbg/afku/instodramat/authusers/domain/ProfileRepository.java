package com.rcbg.afku.instodramat.authusers.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, String>{
    boolean existsByUserId(String userId);
    Optional<Profile> findProfileByProfileId(int profileId);
    Optional<Profile> findProfileByUserId(String userId);

    Page<Profile> findByFollowersContaining(Profile profile, Pageable pageable);

    @Query("SELECT p.followers FROM Profile p WHERE p.profileId = :profileId")
    Page<Profile> findFollowersById(@Param("profileId") Integer profileId, Pageable pageable);
}
