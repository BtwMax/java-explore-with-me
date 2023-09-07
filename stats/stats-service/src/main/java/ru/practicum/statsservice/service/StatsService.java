package ru.practicum.statsservice.service;

import ru.practicum.stats.statsdto.InnerStatsDto;
import ru.practicum.stats.statsdto.OutHitsDto;
import ru.practicum.stats.statsdto.OutStatsDto;

import java.util.List;

public interface StatsService {

    OutStatsDto addStats(InnerStatsDto innerStatsDto);

    List<OutHitsDto> getStats(String start, String end, List<String> uris, boolean unique);
}
