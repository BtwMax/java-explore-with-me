package ru.practicum.ewmservice.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.events.dto.EventShortDto;
import ru.practicum.ewmservice.events.mapper.EventMapper;
import ru.practicum.ewmservice.events.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventsShortDto) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventsShortDto)
                .build();
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilationList) {
        return compilationList.stream()
                .map(compilation -> CompilationDto.builder()
                        .id(compilation.getId())
                        .pinned(compilation.getPinned())
                        .events(EventMapper.toEventShortDtoList(compilation.getEvents()))
                        .title(compilation.getTitle())
                        .build())
                .collect(Collectors.toList());
    }
}
