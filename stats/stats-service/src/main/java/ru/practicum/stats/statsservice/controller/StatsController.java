package ru.practicum.stats.statsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewmservice.stats.statsdto.InnerStatsDto;
import ru.practicum.ewmservice.stats.statsdto.OutHitsDto;
import ru.practicum.stats.statsservice.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping
public class StatsController {

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping(path = "/hit")
    public void addStats(@RequestBody @Valid InnerStatsDto innerStatsDto) {
        log.info("Запрос на добавление статистики посещения ивента");
        statsService.addStats(innerStatsDto);
    }

    @GetMapping(path = "/stats")
    public List<OutHitsDto> getStats(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                          LocalDateTime start,
                                     @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                          LocalDateTime end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос вывод статистики посещений ивента");
        return statsService.getStats(start, end, uris, unique);
    }
}
