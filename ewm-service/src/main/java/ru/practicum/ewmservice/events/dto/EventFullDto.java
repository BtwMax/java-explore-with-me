package ru.practicum.ewmservice.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.events.model.Location;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    private long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private State state;

    private String title;

    private Long views;

}
