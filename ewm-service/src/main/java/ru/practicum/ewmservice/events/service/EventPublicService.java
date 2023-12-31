package ru.practicum.ewmservice.events.service;

import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.EventSearchParams;
import ru.practicum.ewmservice.events.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getPublicEvents(EventSearchParams params, HttpServletRequest request);

    EventFullDto getEventById(long eventId, HttpServletRequest request);
}
