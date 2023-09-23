package ru.practicum.ewmservice.comments.service;

import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    /*Private methods*/
    CommentDto addComment(long eventId, long userId, NewCommentDto newCommentDto);

    CommentDto updateComment(long comId, long userId, NewCommentDto newCommentDto);

    void deleteCommentByUser(long comId, long userId);

    /*Admin methods*/
    List<CommentDto> getUserComments(long userId, int from, int size);

    void deleteCommentByAdmin(long comId);

    /*Public methods*/
    List<CommentDto> getEventComments(long eventId, int from, int size);
}
