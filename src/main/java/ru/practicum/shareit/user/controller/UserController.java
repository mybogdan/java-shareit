package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен GET запрос по эндпоинту /users на получение всех существующих Users.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long userId) {
        log.info("Получен GET запрос по эндпоинту /users/{} на получение User с ID {}.", userId, userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен POST запрос по эндпоинту /users на добавление User {}.", userDto);
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос по эндпоинту /users/{} на обновление данных User с ID {}.", userId, userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long userId) {
        log.info("Получен DELETE запрос по эндпоинту /users/{} на удаление User с ID {}.", userId, userId);
        userService.delete(userId);
    }
}
