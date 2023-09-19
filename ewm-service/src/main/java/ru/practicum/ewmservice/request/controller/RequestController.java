package ru.practicum.ewmservice.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable long userId, @RequestParam long eventId) {
        log.info("Создание запроса от пользователся с id = " + userId + " на участие в событии с id = " + eventId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable long userId) {
        log.info("Получение всез запросов на участие в событиях от пользователя с id = " + userId);
        return requestService.getUserRequests(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("Отмена запроса на участие в мероприятии с id = " + requestId + " от пользователя с id = " + userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
