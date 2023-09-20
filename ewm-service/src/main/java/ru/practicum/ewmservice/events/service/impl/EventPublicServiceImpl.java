package ru.practicum.ewmservice.events.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.EventSearchParams;
import ru.practicum.ewmservice.events.dto.EventShortDto;
import ru.practicum.ewmservice.events.mapper.EventMapper;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.events.service.EventPublicService;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.stats.statsclient.StatsClient;
import ru.practicum.ewmservice.stats.statsdto.GetStatDto;
import ru.practicum.ewmservice.stats.statsdto.InnerStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventPublicServiceImpl implements EventPublicService {


    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Autowired
    public EventPublicServiceImpl(EventRepository eventRepository, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.statsClient = statsClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(EventSearchParams params, HttpServletRequest request) {
        if (params.getRangeEnd() != null && params.getRangeStart() != null &&
                params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new BadRequestException("Неверно задано дата и время");
        }
        Pageable page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        List<Event> events;
        if (params.getRangeEnd() == null && params.getRangeStart() == null) {
            events = eventRepository.findAllByPublicNoDate(params.getText(), params.getCategories(), params.getPaid(),
                    LocalDateTime.now(), page);
        } else {
            events = eventRepository.findAllByPublic(params.getText(), params.getCategories(), params.getPaid(),
                    params.getRangeStart(), params.getRangeEnd(), page);
        }
        events.forEach(event -> event.setViews(getEventStat(request)));
        if (params.getSorts() != null) {
            switch (params.getSorts()) {
                case EVENT_DATE:
                    events.sort(Comparator.comparing(Event::getEventDate));
                    break;
                case VIEWS:
                    events.sort(Comparator.comparing(Event::getViews));
                    break;
            }
        }
        if (params.getOnlyAvailable()) {
            List<Event> availableEvents = events.stream().filter(eventDto -> eventDto.getParticipantLimit() > eventDto.getConfirmedRequests()
                    || eventDto.getParticipantLimit() == 0).collect(Collectors.toList());
            return availableEvents.stream()
                    .map(EventMapper::toEventDtoShort)
                    .collect(Collectors.toList());
        }
        addEndpointHit(request);
        return events.stream()
                .map(EventMapper::toEventDtoShort)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие не найдено");
        }
        event.setViews(getEventStat(request));
        addEndpointHit(request);
        return EventMapper.toEventFullDto(event);
    }

    private void addEndpointHit(HttpServletRequest request) {
        String app = "ewm-main-service";
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        InnerStatsDto innerStatsDto = InnerStatsDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timeStamp(LocalDateTime.now())
                .build();

        statsClient.addStats(innerStatsDto);
    }

    private long getEventStat(HttpServletRequest request) {
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now().plusYears(50);
        GetStatDto getStatDto = new GetStatDto(start, end, new String[]{request.getRequestURI()}, true);
        return statsClient.getStat(getStatDto);
    }
}
