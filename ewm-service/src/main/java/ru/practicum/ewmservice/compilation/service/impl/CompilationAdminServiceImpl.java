package ru.practicum.ewmservice.compilation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.ewmservice.compilation.mapper.CompilationMapper;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.compilation.repository.CompilationRepository;
import ru.practicum.ewmservice.compilation.service.CompilationAdminService;
import ru.practicum.ewmservice.events.dto.EventShortDto;
import ru.practicum.ewmservice.events.mapper.EventMapper;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationAdminServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        Compilation compilationStorage = compilationRepository.save(compilation);
        List<EventShortDto> shortEvents = events.stream()
                .map(EventMapper::toEventDtoShort)
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilationStorage, shortEvents);
    }

    @Override
    public CompilationDto updateCompilationById(Long compId, UpdateCompilationRequestDto updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(()
                -> new NotFoundException("Подборка с id = " + compId + " не найдена"));
        if (!CollectionUtils.isEmpty(updateCompilation.getEvents())) {
            List<Event> events = eventRepository.findAllByIdIn(updateCompilation.getEvents());
            compilation.setEvents(events);
        }
        List<Event> events = compilation.getEvents();
        Optional.ofNullable(updateCompilation.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilation.getPinned()).ifPresent(compilation::setPinned);
        List<EventShortDto> shortEvents = events.stream()
                .map(EventMapper::toEventDtoShort)
                .collect(Collectors.toList());
        Compilation compilationStorage = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilationStorage, shortEvents);
    }

    @Override
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + " не найдена"));
        compilationRepository.deleteById(compilation.getId());
    }
}
