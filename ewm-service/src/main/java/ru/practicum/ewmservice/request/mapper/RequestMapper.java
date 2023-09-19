package ru.practicum.ewmservice.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.request.model.ParticipationRequest;
import ru.practicum.ewmservice.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public ParticipationRequest toParticipationRequest(LocalDateTime dateTime, User requester, Event event) {
        return ParticipationRequest.builder()
                .created(dateTime)
                .requester(requester)
                .event(event)
                .build();
    }

    public EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(
            List<ParticipationRequest> confirmedRequests, List<ParticipationRequest> rejectedRequests) {

        List<ParticipationRequestDto> confirmedRequestsDtoList = confirmedRequests.stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequestsDtoList = rejectedRequests.stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequestsDtoList, rejectedRequestsDtoList);
    }
}
