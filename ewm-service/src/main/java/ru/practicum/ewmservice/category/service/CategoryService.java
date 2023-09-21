package ru.practicum.ewmservice.category.service;

import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    /* Admin methods */
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(long id, NewCategoryDto newCategoryDto);

    void deleteCategoryById(long id);


    /* Public methods */
    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(long id);
}
