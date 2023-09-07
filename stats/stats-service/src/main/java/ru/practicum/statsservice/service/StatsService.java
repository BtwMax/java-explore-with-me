package ru.practicum.statsservice.service;

import ru.practicum.stats.statsdto.InnerStatsDto;
import ru.practicum.stats.statsdto.OutHitsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addStats(InnerStatsDto innerStatsDto);

    List<OutHitsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
