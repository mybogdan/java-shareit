package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.memory.InMemoryUserStorageImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorageImpl userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUsersDto(userStorage.getAllUsers());
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (!userStorage.userExistValidation(userId)) {
            log.info("User с ID {} не найден .", userId);
            throw new EntityNotFoundException("Такого пользователя не существует.");
        }
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
            log.info("Некорректный email.");
            throw new BadRequestException("Некорректный email.");
        }
        if (userStorage.duplicateCheck(userDto.getEmail())) {
            log.info("Некорректный запрос. Данный Email уже существует.");
            throw new EmailDuplicateException("Пользователь уже существует.");
        }
        User user = userStorage.addUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (!userStorage.userExistValidation(userId)) {
            log.info("User с ID {} не найден .", userId);
            throw new EntityNotFoundException("Такого пользователя не существует.");
        }
        User user = userStorage.updateUser(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public Boolean delete(Long userId) {
        if (!userStorage.userExistValidation(userId)) {
            log.info("User с ID {} не найден .", userId);
            throw new EntityNotFoundException("Такого пользователя не существует.");
        }
        return userStorage.deleteUser(userId);
    }
}
