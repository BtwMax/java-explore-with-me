package ru.practicum.ewmservice.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.events.model.enums.Status;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.request.mapper.RequestMapper;
import ru.practicum.ewmservice.request.model.ParticipationRequest;
import ru.practicum.ewmservice.request.repository.RequestRepository;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public RequestServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                              RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id = %s was not found.", eventId)));

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id = %s was not found.", userId));
        }
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("Создатель ивента не может отправить запрос на участие в своем событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Невозможно отправить запрос к неопубликованному событию");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            throw new ConflictException("Запрос на участие в событии уже отправлен");
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }
        ParticipationRequest request = RequestMapper.toParticipationRequest(LocalDateTime.now(), user, event);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request.setStatus(Status.PENDING);
        }
        ParticipationRequest participationRequestStorage = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(participationRequestStorage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id = %s is not found.", userId));
        }
        return requestRepository.findAllByRequester(user).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id = %s is not found.", userId));
        }
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id = %s is not found.", requestId)));
        if (!(request.getRequester().getId() == userId)) {
            throw new ConflictException(String.format("Пользователь с id = %s не является хозяином запроса", userId));
        }
        request.setStatus(Status.CANCELED);
        ParticipationRequest requestStorage = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(requestStorage);
    }
}
