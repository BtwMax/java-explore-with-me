package ru.practicum.ewmservice.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class CategoryDto {

    private long id;
    private String name;

    public CategoryDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
