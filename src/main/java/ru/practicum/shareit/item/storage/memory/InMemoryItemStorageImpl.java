package ru.practicum.shareit.item.storage.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.ItemUpdater;

import java.util.*;

@Repository
@Slf4j
@Primary
@RequiredArgsConstructor
public class InMemoryItemStorageImpl implements ItemStorage {

    private final UserServiceImpl userServiceImpl;
    private final Map<Long, Item> items = new HashMap<>();
    private Long idItems = 0L;

    @Override
    public Item addItem(Long userId, Item item) {
        item.setId(++idItems);
        item.setOwner(UserMapper.toUser(userServiceImpl.getUserById(userId)));
        items.put(item.getId(), item);
        log.info("User(Owner) c ID {} создал Item c ID {}.", userId, item.getId());
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item oldItem = items.get(itemId);

        if (!Objects.equals(oldItem.getOwner().getId(), userId)) {
            log.info("User с ID {} не является owner.", userId);
            throw new ResponseStatusException(HttpStatus.valueOf(404), "User с ID {} не является owner.");
        }
        ItemUpdater.itemPatch(oldItem, item);
        log.info("User(Owner) c ID {} обновил данные Item c ID {}.", userId, oldItem.getId());
        return oldItem;
    }

    @Override
    public Item getItemById(Long userId, Long itemId) {
        Item item = items.get(itemId);
        log.info("User c ID {} получил данные Item c ID {}.", userId, itemId);
        return item;
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                result.add(item);
            }
        }

        log.info("User(Owner) c ID {} получил список всех своих Item.", userId);

        return result;
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> result = new ArrayList<>();

        for (Item i : items.values()) {
            if (i.getName().toLowerCase().contains(text.toLowerCase())
                    || i.getDescription().toLowerCase().contains(text.toLowerCase())
                    && i.getAvailable()) {
                result.add(i);
            }
        }

        log.info("User с ID {} получил результаты поиска по запросу {}.", userId, text);

        return result;
    }

    @Override
    public Boolean deleteItem(Long userId, Long itemId) {
        if (!Objects.equals(items.get(itemId).getOwner().getId(), userId)) {
            log.info("User с ID {} не является owner.", userId);
            throw new ResponseStatusException(HttpStatus.valueOf(404), "User с ID {} не является owner.");
        }
        items.remove(itemId);
        log.info("User(Owner) с ID {} удалил  Item c ID {}.", userId, itemId);
        return true;
    }

    @Override
    public Boolean isItemExists(Long id) {
        return items.containsKey(id);
    }
}