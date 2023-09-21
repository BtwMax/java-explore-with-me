package ru.practicum.ewmservice.events.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.practicum.ewmservice.events.model.enums.Sorts;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Getter
public class EventSearchParams {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Sorts sorts;
    private Integer from;
    private Integer size;
}
