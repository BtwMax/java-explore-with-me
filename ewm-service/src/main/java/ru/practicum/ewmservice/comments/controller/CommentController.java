package ru.practicum.ewmservice.comments.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments/{eventId}/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long eventId,
                                 @PathVariable long userId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Запрос на добавление комментария");
        return commentService.addComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("/comments/{userId}/{comId}")
    public CommentDto updateComment(@PathVariable long userId,
                                    @PathVariable long comId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Запрос на изменение комментария");
        return commentService.updateComment(comId, userId, newCommentDto);
    }

    @DeleteMapping("/comments/{userId}/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable long userId, @PathVariable long comId) {
        log.info("Запрос от пользователя на удаление своего комментария");
        commentService.deleteCommentByUser(comId, userId);
    }

    @GetMapping("/admin/comments/{userId}")
    public List<CommentDto> getUserComments(@PathVariable long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос админа на вывод всех комментариев пользователя с id = " + userId);
        return commentService.getUserComments(userId, from, size);
    }

    @DeleteMapping("/admin/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable long comId) {
        log.info("Запрос админа на удаление комментария с id = " + comId);
        commentService.deleteCommentByAdmin(comId);
    }

    @GetMapping("comments/{eventId}")
    public List<CommentDto> getEventComments(@PathVariable long eventId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на показ комментариев у события с id = " + eventId);
        return commentService.getEventComments(eventId, from, size);
    }
}
