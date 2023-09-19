package ru.practicum.ewmservice.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.service.CompilationPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationPublicService publicService;

    @Autowired
    public CompilationPublicController(CompilationPublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение списка всех подборок");
        return publicService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("GET запрос на получение подборки с ID = {}", compId);
        return publicService.getCompilationById(compId);
    }
}
