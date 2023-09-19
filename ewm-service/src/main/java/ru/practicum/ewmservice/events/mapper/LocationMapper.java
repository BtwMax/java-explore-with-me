package ru.practicum.ewmservice.events.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.events.dto.LocationDto;
import ru.practicum.ewmservice.events.model.Location;

@UtilityClass
public class LocationMapper {

    public Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
