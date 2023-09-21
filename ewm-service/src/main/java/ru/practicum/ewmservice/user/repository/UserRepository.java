package ru.practicum.ewmservice.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);

    @Query("SELECT new ru.practicum.ewmservice.user.dto.UserDto(u.id, u.name, u.email) " +
            "FROM User AS u")
    List<UserDto> getAllUsers(Pageable pageable);

    @Query("SELECT new ru.practicum.ewmservice.user.dto.UserDto(u.id, u.name, u.email) " +
            "FROM User AS u " +
            "WHERE u.id IN :ids")
    List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable);

    User findUserByEmail(String email);
}
