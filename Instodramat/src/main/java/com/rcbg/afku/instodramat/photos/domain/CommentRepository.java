package com.rcbg.afku.instodramat.photos.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByPhotoPhotoId(int photoId, Pageable pageable);

    Optional<Comment> findCommentByPhotoPhotoIdAndCommentId(int photoId, int commentId);
}
