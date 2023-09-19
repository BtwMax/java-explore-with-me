package ru.practicum.ewmservice.events.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.category.repository.CategoryRepository;
import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewmservice.events.mapper.EventMapper;
import ru.practicum.ewmservice.events.mapper.LocationMapper;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.Location;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.events.model.enums.StateAction;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.events.repository.LocationRepository;
import ru.practicum.ewmservice.events.service.EventAdminService;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public EventAdminServiceImpl(EventRepository eventRepository, RequestRepository requestRepository,
                                 LocationRepository locationRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(List<Long> users, List<State> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           int from, int size) {
        if (rangeEnd != null && rangeStart != null && rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Неверно задано дата и время");
        }
        Pageable page = PageRequest.of(from / size, size);
        List<EventFullDto> events = eventRepository.findAllAdminByData(users, states, categories, rangeStart,
                        rangeEnd, page)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        events.forEach(e ->
                e.setConfirmedRequests(requestRepository.findConfirmedRequests(e.getId())));
        return events;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found.", eventId)));

        if ((updateEventAdminRequest.getStateAction() != null) &&
                (updateEventAdminRequest.getStateAction() != StateAction.PUBLISH_EVENT
                        && updateEventAdminRequest.getStateAction() != StateAction.REJECT_EVENT)) {
            throw new ConflictException("StateAction может принимать значения: PUBLISH_EVENT или REJECT_EVENT");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT) &&
                    event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Нельзя отклонить уже опубликованное событие");
            }
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT) && (
                    !event.getState().equals(State.PENDING))) {
                throw new ConflictException("Чтобы опубликовать событие оно должно быть в статусе PENDING");
            }
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        }

        if ((updateEventAdminRequest.getEventDate() != null) &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Время начала события не может быть ранее чем через 2 часа от текущего");
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationRepository.findByLatAndLon(updateEventAdminRequest.getLocation().getLat(),
                    updateEventAdminRequest.getLocation().getLon()).orElseGet(() ->
                    locationRepository.save(LocationMapper.toLocation(updateEventAdminRequest.getLocation())));
            event.setLocation(location);
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory());
            if (category == null) {
                throw new NotFoundException(String.format("Category with id=%s was not found.",
                        updateEventAdminRequest.getCategory()));
            }
            event.setCategory(category);
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        Event updatedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(updatedEvent);
        eventFullDto.setConfirmedRequests(requestRepository.findConfirmedRequests(eventFullDto.getId()));
        return eventFullDto;
    }
}
