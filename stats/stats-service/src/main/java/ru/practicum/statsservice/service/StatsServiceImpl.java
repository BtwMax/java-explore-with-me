package ru.practicum.statsservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stats.statsdto.InnerStatsDto;
import ru.practicum.stats.statsdto.OutHitsDto;
import ru.practicum.stats.statsdto.OutStatsDto;
import ru.practicum.statsservice.mapper.StatsMapper;
import ru.practicum.statsservice.model.Stats;
import ru.practicum.statsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    public List<OutHitsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        List<OutHitsDto> hitsDto = new ArrayList<>();
        if (uris == null || uris.size() == 0) {
            if (unique) {
                hitsDto = statsRepository.getAllUniqueIpHits(LocalDateTime.parse(start, format),
                        LocalDateTime.parse(end, format));
            } else {
                hitsDto = statsRepository.getAllNotUniqueIpHits(LocalDateTime.parse(start, format),
                        LocalDateTime.parse(end, format));
            }
        } else {
            for (String uri : uris) {
                Optional<OutHitsDto> stat;
                if (unique) {
                    stat = statsRepository.getByUriAndUniqueIpHits(uri,
                            LocalDateTime.parse(start, format),
                            LocalDateTime.parse(end, format));
                } else {
                    stat = statsRepository.getByUriAndNotUniqueIpHits(uri,
                            LocalDateTime.parse(start, format),
                            LocalDateTime.parse(end, format));
                }
                if (stat.isPresent()) {
                    hitsDto.add(stat.get());
                }
            }
            if (!hitsDto.isEmpty()) {
                hitsDto = hitsDto.stream()
                        .sorted(Comparator.comparing(OutHitsDto::getHits).reversed())
                        .collect(Collectors.toList());
            }
        }
        return hitsDto;
    }
}
