package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на поиск всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long userId) {
        log.info("Запрос на получение пользователя по id.");
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя.");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя.");
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable("id") Long userId) {
        log.info("Запрос на удаление пользователя.");
        return userService.delete(userId);
    }
}
