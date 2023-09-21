package ru.practicum.ewmservice.events.service;

import ru.practicum.ewmservice.events.dto.*;
import ru.practicum.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {

    EventFullDto addNewEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventsByUserId(long userId, int from, int size);

    EventFullDto getEventByUser(long eventId, long userId);

    EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getAllEventParticipationRequests(long userId, long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId,
                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
