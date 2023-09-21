package ru.practicum.ewmservice.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.category.repository.CategoryRepository;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    /*Admin methods*/
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.findByName(newCategoryDto.getName()) != null) {
            throw new ConflictException(String.format("Category with name: %s is exist.", newCategoryDto.getName()));
        }
        Category category = CategoryMapper.toCategory(newCategoryDto);
        Category categoryStorage = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(categoryStorage);
    }

    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId);
        if (category == null) {
            throw new NotFoundException(String.format("Category with id=%s was not found.", catId));
        }
        if (categoryRepository.findByNameAndIdNot(newCategoryDto.getName(), catId) != null) {
            throw new ConflictException(String.format(
                    "Категория с параметрами: %d, %s уже существует", catId, newCategoryDto.getName()));
        }
        category.setName(newCategoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategoryById(long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException(String.format("Category with id=%s was not found.", id));
        }
        if (!CollectionUtils.isEmpty(eventRepository.findAllByCategoryId(id))) {
            throw new ConflictException(String.format(
                    "Категория с ID = %d не удалена т.к. в ней есть события", id));
        }
        categoryRepository.deleteById(id);
    }


    /*Public methods*/
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.getAllCategories(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException(String.format("Category with id=%s was not found.", id));
        }
        return CategoryMapper.toCategoryDto(category);
    }
}
