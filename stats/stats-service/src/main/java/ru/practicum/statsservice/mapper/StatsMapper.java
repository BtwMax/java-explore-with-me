package ru.practicum.statsservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.statsdto.InnerStatsDto;
import ru.practicum.stats.statsdto.OutStatsDto;
import ru.practicum.statsservice.model.Stats;

@UtilityClass
public class StatsMapper {

    public Stats toStats(InnerStatsDto innerStatsDto) {
        return Stats.builder()
                .app(innerStatsDto.getApp())
                .uri(innerStatsDto.getUri())
                .ip(innerStatsDto.getIp())
                .timestamp(innerStatsDto.getTimeStamp())
                .build();
    }

    public OutStatsDto toOutStats(Stats stats) {
        return OutStatsDto.builder()
                .id(stats.getId())
                .uri(stats.getUri())
                .ip(stats.getIp())
                .timestamp(stats.getTimestamp())
                .build();
    }
}
