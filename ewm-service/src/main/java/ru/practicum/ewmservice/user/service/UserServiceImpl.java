package ru.practicum.ewmservice.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.dto.IncomeUserDto;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.mapper.UserMapper;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(IncomeUserDto incomeUserDto) {
        if (userRepository.findUserByEmail(incomeUserDto.getEmail()) != null) {
            throw new ConflictException(String.format("User with email: %s is exist.", incomeUserDto.getEmail()));
        }
        User user = UserMapper.toUser(incomeUserDto);
        User userStorage = userRepository.save(user);
        return UserMapper.toUserDto(userStorage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        List<UserDto> users;
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = userRepository.getAllUsers(pageable);
        } else {
            users = userRepository.getUsersByIds(ids, pageable);
        }
        return users;
    }

    @Override
    public void deleteUserById(long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException(String.format("User with id=%s was not found.", id));
        }
        userRepository.deleteById(id);
    }
}
