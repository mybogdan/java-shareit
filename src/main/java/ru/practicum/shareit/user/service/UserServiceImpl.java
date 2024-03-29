package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExist;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        return repository.findById(userId).map(UserMapper::toUserDto)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден."));
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
            log.info("Некорректный email.");
            throw new IllegalArgumentException("Некорректный email.");
        }
        if (repository.existsUserByEmail(userDto.getEmail())) {
            repository.save(UserMapper.toUser(userDto));
            log.info("Пользователь уже существует.");
            throw new EntityAlreadyExist("Пользователь уже существует.");
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        User updateUser = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден."));
        return UserMapper.toUserDto(repository.save(updateNameAndEmailUser(updateUser, userDto)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!repository.existsById(userId)) {
            log.info("Пользователь не найден.");
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        repository.deleteById(userId);
    }

    private User updateNameAndEmailUser(User updatedUser, UserDto user) {

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (!repository.existsUserByEmail(user.getEmail())) {
                updatedUser.setEmail(user.getEmail());
            } else {
                log.info("Такой email уже существует.");
                throw new EntityAlreadyExist("Такой email уже существует.");
            }
        }
        return updatedUser;
    }
}
