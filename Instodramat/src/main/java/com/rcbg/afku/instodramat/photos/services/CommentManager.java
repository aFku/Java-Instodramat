package com.rcbg.afku.instodramat.photos.services;

import com.rcbg.afku.instodramat.authusers.domain.Profile;
import com.rcbg.afku.instodramat.authusers.services.ProfileManager;
import com.rcbg.afku.instodramat.photos.domain.Comment;
import com.rcbg.afku.instodramat.photos.domain.CommentRepository;
import com.rcbg.afku.instodramat.photos.domain.Photo;
import com.rcbg.afku.instodramat.photos.dtos.CommentMapper;
import com.rcbg.afku.instodramat.photos.dtos.CommentRequestDto;
import com.rcbg.afku.instodramat.photos.dtos.CommentResponseDto;
import com.rcbg.afku.instodramat.photos.exceptions.CommentNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Validated
public class CommentManager {

    Logger logger = LoggerFactory.getLogger(CommentManager.class);

    private final CommentRepository repository;

    private final PhotoManager photoManager;

    private final ProfileManager profileManager;

    @Autowired
    CommentManager(CommentRepository repository, PhotoManager photoManager, ProfileManager profileManager){
        this.repository = repository;
        this.photoManager = photoManager;
        this.profileManager = profileManager;
    }

    private Comment getCommentThatBelongsToPhoto(int photoId, int commentId){
        return repository.findCommentByPhotoPhotoIdAndCommentId(photoId, commentId).orElseThrow(() -> new CommentNotFoundException(
                "Comment with ID: " + commentId + " does not belong to Photo with ID: " + photoId));
    }

    public Comment getCommentDomainObjectById(int commentId){
        return repository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment with ID: " + commentId + " does not exists"));
    }

    public boolean checkOwnership(int commentId, String userId){
        Profile profile = profileManager.getDomainObjectByUserId(userId);
        Comment comment = this.getCommentDomainObjectById(commentId);
        return Objects.equals(comment.getAuthor().getProfileId(), profile.getProfileId());
    }

    public Page<CommentResponseDto> getCommentsFromPhoto(int photoId, Pageable pageable){
        photoManager.getDomainObjectByPhotoId(photoId); // check if photo exists
        return repository.findAllByPhotoPhotoId(photoId, pageable).map(CommentMapper.INSTANCE::EntityToResponseDto);
    }

    public CommentResponseDto createCommentForPhoto(@Valid CommentRequestDto requestDto, int photoId, String userId){
        Photo photo = photoManager.getDomainObjectByPhotoId(photoId);
        Profile author = profileManager.getDomainObjectByUserId(userId);
        LocalDateTime publishDate = LocalDateTime.now();
        Comment comment = CommentMapper.INSTANCE.requestDtoToEntity(requestDto, publishDate, author, photo);
        repository.save(comment);
        logger.info("UserId: " + userId + " created comment with ID: " + comment.getCommentId() + " for photo ID: " + photoId);
        return CommentMapper.INSTANCE.EntityToResponseDto(comment);
    }

    public void deleteCommentFromPhotoById(int photoId, int commentId){
        Comment comment = this.getCommentThatBelongsToPhoto(photoId, commentId);
        repository.delete(comment);
        logger.info("Comment with ID: " + commentId + " has been deleted");
    }
}
