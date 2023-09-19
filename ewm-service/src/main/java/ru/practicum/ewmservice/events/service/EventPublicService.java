package ru.practicum.ewmservice.events.service;

import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.EventShortDto;
import ru.practicum.ewmservice.events.model.enums.Sorts;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, Sorts sorts, Integer from,
                                        Integer size, HttpServletRequest request);

    EventFullDto getEventById(long eventId, HttpServletRequest request);
}
