package ru.practicum.ewmservice.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.user.dto.IncomeUserDto;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.dto.UserShortDto;
import ru.practicum.ewmservice.user.model.User;

@UtilityClass
public class UserMapper {

    public User toUser(IncomeUserDto incomeUserDto) {
        return User.builder()
                .name(incomeUserDto.getName())
                .email(incomeUserDto.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toShortUser(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
