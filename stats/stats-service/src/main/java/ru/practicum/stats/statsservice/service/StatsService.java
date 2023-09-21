package ru.practicum.stats.statsservice.service;

import ru.practicum.ewmservice.stats.statsdto.InnerStatsDto;
import ru.practicum.ewmservice.stats.statsdto.OutHitsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addStats(InnerStatsDto innerStatsDto);

    List<OutHitsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
