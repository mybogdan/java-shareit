package ru.practicum.shareit.user.storage.memory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();
    private Long idUsers = 0L;

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User addUser(User user) {
        user.setId(++idUsers);
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {

        User updateUser = getUserById(userId);
        String emailUser = updateUser.getEmail();

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            if (!(isUserExistsByEmail(user.getEmail()))) {
                updateUser.setEmail(user.getEmail());
            } else
                throw new UserAlreadyExist("Такой email уже существует.");
        }

        emails.remove(emailUser);
        emails.put(user.getEmail(), user.getId());
        user.setId(userId);
        users.put(userId, updateUser);

        return updateUser;
    }

    @Override
    public Boolean deleteUser(Long userId) {

        emails.remove(getUserById(userId).getEmail());
        users.remove(userId);

        return !(users.containsKey(userId));
    }

    @Override
    public Boolean isUserExistsById(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Boolean isUserExistsByEmail(String email) {
        return emails.containsKey(email);
    }
}
