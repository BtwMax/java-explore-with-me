package ru.practicum.ewmservice.events.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.events.service.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService adminService;

    @Autowired
    public EventAdminController(EventAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка событий");
        return adminService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Запрос на обновление события с id = " + eventId);
        return adminService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}
