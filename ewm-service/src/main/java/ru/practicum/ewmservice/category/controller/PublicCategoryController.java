package ru.practicum.ewmservice.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @Autowired
    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") @Positive long catId) {
        return categoryService.getCategoryById(catId);
    }
}
