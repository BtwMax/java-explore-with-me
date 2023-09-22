package ru.practicum.ewmservice.comments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.comments.dto.CommentDto;
import ru.practicum.ewmservice.comments.dto.NewCommentDto;
import ru.practicum.ewmservice.comments.mapper.CommentMapper;
import ru.practicum.ewmservice.comments.model.Comment;
import ru.practicum.ewmservice.comments.repository.CommentRepository;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /*Private methods*/
    @Override
    public CommentDto addComment(long eventId, long userId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", userId));
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с  id = %s не найдено", eventId)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя оставлять комментарий к неопубликованному событию");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, user, event);
        Comment commentStorage = commentRepository.save(comment);
        return CommentMapper.toCommentDto(commentStorage);
    }

    @Override
    public CommentDto updateComment(long comId, long userId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(comId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с  id = %s не найден", comId)));
        if (comment.getAuthor().getId() != userId) {
            throw new ConflictException(String.format(
                    "Пользователь с  id = %s не является автором комментария с id = %s", userId, comId));
        }
        comment.setText(newCommentDto.getText());
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteCommentByUser(long comId, long userId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с  id = %s не найден", comId)));
        if (comment.getAuthor().getId() != userId) {
            throw new ConflictException(String.format(
                    "Пользователь с  id = %s не является автором комментария с id = %s", userId, comId));
        }
        commentRepository.deleteById(comId);
    }

    /*Admin methods*/
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(long userId, int from, int size) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", userId));
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> userComments = commentRepository.findCommentsByAuthorId(userId, pageable);
        if (userComments.isEmpty()) {
            return new ArrayList<>();
        }
        return userComments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByAdmin(long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с  id = %s не найден", comId)));
        commentRepository.deleteById(comment.getId());
    }

    /*Public methods*/
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(long eventId, int from, int size) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с  id = %s не найдено", eventId)));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> eventComments = commentRepository.findCommentsByEventId(event.getId(), pageable);
        if (eventComments.isEmpty()) {
            return new ArrayList<>();
        }
        return eventComments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
