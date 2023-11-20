package ru.practicum.shareit.user.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();
    private Long idUsers = 0L;

    public List<User> getAllUsers() {
        log.info("Список всех существующих User получен.");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        log.info("User с ID {} получен.", userId);
        return users.get(userId);
    }

    @Override
    public User addUser(User user) {
        user.setId(++idUsers);
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        log.info("User c ID {} создан.", user.getId());
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
            if (!(duplicateCheck(user.getEmail()))) {
                updateUser.setEmail(user.getEmail());
            } else {
                log.info("Некорректный запрос. Данный Email уже существует.");
                throw new EmailDuplicateException("Такой Email уже существует.");
            }
        }
        emails.remove(emailUser);
        emails.put(user.getEmail(), user.getId());
        user.setId(userId);
        users.put(userId, updateUser);
        log.info("Данные User с ID {} обновлены.", userId);
        return updateUser;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        emails.remove(getUserById(userId).getEmail());
        users.remove(userId);
        log.info("User с ID {} удалён.", userId);
        return !(users.containsKey(userId));
    }

    @Override
    public Boolean userExistValidation(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Boolean duplicateCheck(String email) {
        return emails.containsKey(email);
    }
}
