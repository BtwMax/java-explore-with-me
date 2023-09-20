package ru.practicum.ewmservice.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.category.repository.CategoryRepository;
import ru.practicum.ewmservice.events.dto.*;
import ru.practicum.ewmservice.events.mapper.EventMapper;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.Location;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.events.model.enums.StateAction;
import ru.practicum.ewmservice.events.model.enums.Status;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.events.repository.LocationRepository;
import ru.practicum.ewmservice.events.service.EventPrivateService;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.request.mapper.RequestMapper;
import ru.practicum.ewmservice.request.model.ParticipationRequest;
import ru.practicum.ewmservice.request.repository.RequestRepository;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventPrivateServiceImpl implements EventPrivateService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Override
    public EventFullDto addNewEvent(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Неверно заданы дата и время");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s was not found.", userId));
        }

        Category category = categoryRepository.findById(newEventDto.getCategory());
        if (category == null) {
            throw new NotFoundException(String.format("Category with id=%s was not found.", newEventDto.getCategory()));
        }

        Location location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon()).orElseGet(() ->
                locationRepository.save(newEventDto.getLocation()));

        Event event = EventMapper.toEvent(newEventDto, category, location, user, LocalDateTime.now(), State.PENDING);
        Event eventStorage = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventStorage);
        eventFullDto.setConfirmedRequests(requestRepository.findConfirmedRequests(eventFullDto.getId()));
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventsByUserId(long userId, int from, int size) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s was not found.", userId));
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::toEventDtoShort)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByUser(long eventId, long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s was not found.", userId));
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s was not found.", userId));
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        if (event.getState() != State.PENDING && event.getState() != State.CANCELED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }

        if (updateEventUserRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        }
        if (updateEventUserRequest.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Неверно заданы дата и время");
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationRepository.findByLatAndLon(updateEventUserRequest.getLocation().getLat(),
                    updateEventUserRequest.getLocation().getLon()).orElseGet(() ->
                    locationRepository.save(updateEventUserRequest.getLocation()));
            event.setLocation(location);
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory());
            if (category == null) {
                throw new NotFoundException(String.format("Category with id=%s was not found.",
                        updateEventUserRequest.getCategory()));
            }
            event.setCategory(category);
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        Event updatedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(updatedEvent);
        eventFullDto.setConfirmedRequests(requestRepository.findConfirmedRequests(eventFullDto.getId()));
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllEventParticipationRequests(long userId, long eventId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s was not found.", userId));
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException(String.format("Пользователь с id=%s не является создателем события", userId));
        }
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("No such event"));

        List<Long> requestIds = updateRequest.getRequestIds();
        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);
        if (requests.size() != requestIds.size()) {
            throw new NotFoundException("No such request");
        }

        for (ParticipationRequest request : requests) {
            if (request.getStatus() == Status.CONFIRMED && updateRequest.getStatus() == Status.REJECTED) {
                throw new ConflictException("Can't reject confirmed request");
            }
            request.setStatus(updateRequest.getStatus());

            if (updateRequest.getStatus() == Status.CONFIRMED) {
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                if (event.getConfirmedRequests() > event.getParticipantLimit()) {
                    throw new ConflictException("Participation limit exceeded");
                }
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }

            if (updateRequest.getStatus() == Status.REJECTED) {
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }

            requestRepository.save(request);
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (!confirmedRequests.isEmpty()) {
            result.setConfirmedRequests(confirmedRequests);
        }
        if (!rejectedRequests.isEmpty()) {
            result.setRejectedRequests(rejectedRequests);
        }

        return result;
    }

    private List<ParticipationRequest> confirmRequests(List<Long> requestIds, Event event) {
        long availableSlots = event.getParticipantLimit() - requestRepository.findConfirmedRequests(event.getId());
        long numOfConfirmedRequests = Math.min(requestIds.size(), availableSlots);

        List<ParticipationRequest> requestsList = requestRepository.findAllByIdIn(requestIds);

        return requestsList.stream()
                .filter(request -> request.getStatus() != Status.CONFIRMED)
                .limit(numOfConfirmedRequests)
                .peek(request -> request.setStatus(Status.CONFIRMED))
                .collect(Collectors.toList());
    }

    private List<ParticipationRequest> rejectRemainingRequests(List<Long> requestIds) {
        List<ParticipationRequest> requestsList = requestRepository.findAllByIdIn(requestIds);

        return requestsList.stream()
                .filter(request -> !request.getStatus().equals(Status.CONFIRMED))
                .peek(request -> request.setStatus(Status.REJECTED))
                .collect(Collectors.toList());
    }

    private List<ParticipationRequest> rejectRequests(List<Long> requestIds) {
        List<ParticipationRequest> requestsList = requestRepository.findAllByIdIn(requestIds);

        return requestsList.stream()
                .filter(request -> request.getStatus() != Status.REJECTED)
                .peek(request -> request.setStatus(Status.REJECTED))
                .collect(Collectors.toList());
    }
}
