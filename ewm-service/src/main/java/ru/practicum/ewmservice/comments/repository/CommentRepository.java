package ru.practicum.ewmservice.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.comments.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByAuthorId(long userId, Pageable pageable);

    List<Comment> findCommentsByEventId(long eventId, Pageable pageable);

}
