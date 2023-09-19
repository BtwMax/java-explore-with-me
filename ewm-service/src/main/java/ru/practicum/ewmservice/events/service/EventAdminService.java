package ru.practicum.ewmservice.events.service;

import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewmservice.events.model.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {

    List<EventFullDto> searchEvents(List<Long> users, List<State> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
