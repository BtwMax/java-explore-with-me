package ru.practicum.ewmservice.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmservice.events.model.enums.Status;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    List<Long> requestIds;

    @NotNull
    Status status;
}
