package ru.practicum.stats.statsservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.stats.statsdto.InnerStatsDto;
import ru.practicum.ewmservice.stats.statsdto.OutHitsDto;
import ru.practicum.stats.statsservice.repository.StatsRepository;
import ru.practicum.stats.statsservice.mapper.StatsMapper;
import ru.practicum.stats.statsservice.model.Stats;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
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
