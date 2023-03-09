package com.rcbg.afku.instodramat.photos.domain;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {

    @Query("SELECT p.likes FROM Photo p WHERE p.photoId = :photoId")
    Page<Profile> getLikeList(@Param("photoId") Integer photoId, Pageable pageable);

    Page<Photo> findAllByAuthorProfileId(int profileId, Pageable pageable);

    @Query("SELECT ph FROM Photo ph WHERE ph.author IN (SELECT pr.followers FROM Profile pr WHERE pr.profileId = :profileId)")
    Page<Photo> findAllFromFollowers(int profileId, Pageable pageable);
}
