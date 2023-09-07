package ru.practicum.stats.statsdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OutHitsDto {
    private String app;
    private String uri;
    private Integer hits;
}
