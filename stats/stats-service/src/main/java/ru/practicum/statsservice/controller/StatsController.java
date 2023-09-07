package ru.practicum.statsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.stats.statsdto.InnerStatsDto;
import ru.practicum.stats.statsdto.OutHitsDto;
import ru.practicum.stats.statsdto.OutStatsDto;
import ru.practicum.statsservice.service.StatsService;

import javax.validation.Valid;
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
    public OutStatsDto addStats(@RequestBody @Valid InnerStatsDto innerStatsDto) {
        log.info("Запрос на добавление статистики посещения ивента");
        return statsService.addStats(innerStatsDto);
    }

    @GetMapping(path = "/stats")
    public List<OutHitsDto> getStats(@RequestParam String start,
                                     @RequestParam String end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
