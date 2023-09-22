package ru.practicum.ewmservice.comments.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.model.Comment;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .event(event)
                .author(author)
                .text(newCommentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
