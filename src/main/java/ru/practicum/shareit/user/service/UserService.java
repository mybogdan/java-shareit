package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.memory.InMemoryUserStorageImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorageImpl userStorage;

    public List<UserDto> getAllUsers() {
        return UserMapper.toUsersDto(userStorage.getAllUsers());
    }

    public UserDto getUserById(Long userId) {
        if (!userStorage.isUserExistsById(userId)) {
            throw new ObjectNotFoundException("Такого пользователя не существует.");
        }
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Некорректный email.");
        }
        if (userStorage.isUserExistsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExist("Пользователь уже существует.");
        }
        User user = userStorage.addUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        if (!userStorage.isUserExistsById(userId)) {
            throw new ObjectNotFoundException("Такого пользователя не существует.");
        }
        User user = userStorage.updateUser(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public Boolean delete(Long userId) {
        if (!userStorage.isUserExistsById(userId)) {
            throw new ObjectNotFoundException("Такого пользователя не существует.");
        }
        return userStorage.deleteUser(userId);
    }
}
