package ru.practicum.ewmservice.compilation.service;

import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilation.dto.UpdateCompilationRequestDto;

public interface CompilationAdminService {

    CompilationDto createCompilations(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilationById(Long compId, UpdateCompilationRequestDto updateCompilationRequest);

    void deleteCompilationById(Long compId);
}
