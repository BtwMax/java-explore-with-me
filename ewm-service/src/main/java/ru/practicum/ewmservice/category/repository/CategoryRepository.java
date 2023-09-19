package ru.practicum.ewmservice.category.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findById(long id);

    Category findByNameAndIdNot(String name, Long catId);

    @Query("SELECT new ru.practicum.ewmservice.category.dto.CategoryDto(c.id, c.name) " +
            "FROM Category AS c")
    List<CategoryDto> getAllCategories(Pageable pageable);

    Category findByName(String name);

}
