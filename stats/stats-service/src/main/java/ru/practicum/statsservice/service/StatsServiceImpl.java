package ru.practicum.statsservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stats.statsdto.InnerStatsDto;
import ru.practicum.stats.statsdto.OutHitsDto;
import ru.practicum.statsservice.mapper.StatsMapper;
import ru.practicum.statsservice.model.Stats;
import ru.practicum.statsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public void addStats(InnerStatsDto innerStatsDto) {
        Stats stats = StatsMapper.toStats(innerStatsDto);
        statsRepository.save(stats);
    }

    @Override
    public List<OutHitsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<OutHitsDto> options = new ArrayList<>();
        if (uris == null || uris.size() == 0) {
            if (unique) {
                options = statsRepository.findAllUniqueId(start, end);
            } else {
                options = statsRepository.findAllNotUniqueId(start, end);
            }
        } else {
            for (String uri : uris) {
                Optional<OutHitsDto> stat;
                if (unique) {
                    stat = statsRepository.findByUriAndUniqueId(uri, start, end);
                } else {
                    stat = statsRepository.findByUriAndNotUniqueId(uri, start, end);
                }
                if (stat.isPresent()) {
                    options.add(stat.get());
                }
            }
            if (!options.isEmpty()) {
                options = options.stream()
                        .sorted(Comparator.comparing(OutHitsDto::getHits).reversed())
                        .collect(Collectors.toList());
            }
        }
        return options;
    }
}