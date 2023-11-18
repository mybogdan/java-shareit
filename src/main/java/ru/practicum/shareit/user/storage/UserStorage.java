package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User getUserById(Long userId);

    User addUser(User user);

    User updateUser(Long userId, User user);

    Boolean deleteUser(Long userId);

    Boolean isUserExistsById(Long userId);

    Boolean isUserExistsByEmail(String email);
}
