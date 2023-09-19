package ru.practicum.ewmservice.events.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.events.dto.*;
import ru.practicum.ewmservice.events.service.EventPrivateService;
import ru.practicum.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class EventPrivateController {

    private final EventPrivateService privateService;

    @Autowired
    public EventPrivateController(EventPrivateService privateService) {
        this.privateService = privateService;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Запрос на создание события: {} от пользователя с id = {}", newEventDto, userId);
        return privateService.addNewEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка всех событий от пользователя с id = {}", userId);
        return privateService.getAllEventsByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventsByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос на получение от пользователя с id = {} события с id = {}", userId, eventId);
        return privateService.getEventByUser(eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("Запрос на получение запросов события с id = {} от пользователя с id = {}", eventId, userId);
        return privateService.getAllEventParticipationRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Запрос на изменение события с id = {} от пользователя с id = {}", eventId, userId);
        return privateService.updateUserEvent(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusOfRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                                 @Valid @RequestBody
                                                                 EventRequestStatusUpdateRequest EventRequestStatusUpdateRequest) {
        log.info("Запрос на изменение статуса запросов события с id = {} от пользователя с id = {}", eventId, userId);
        return privateService.changeRequestStatus(userId, eventId, EventRequestStatusUpdateRequest);
    }
}
