package ru.practicum.ewmservice.user.service;

import ru.practicum.ewmservice.user.dto.IncomeUserDto;
import ru.practicum.ewmservice.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(IncomeUserDto incomeUserDto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUserById(long id);
}
