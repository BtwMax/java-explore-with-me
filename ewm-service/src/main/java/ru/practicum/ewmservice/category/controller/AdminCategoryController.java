package ru.practicum.ewmservice.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Запрос на добавление новой категории");
        return categoryService.addCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") @Positive long catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Запрос на обновление категории с id = " + catId);
        return categoryService.updateCategory(catId, newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("catId") @Positive long catId) {
        log.info("Запрос на удаление категории с id = " + catId);
        categoryService.deleteCategoryById(catId);
    }
}
