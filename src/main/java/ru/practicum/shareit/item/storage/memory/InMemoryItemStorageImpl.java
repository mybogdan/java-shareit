package ru.practicum.shareit.item.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validator.ItemValidator;

import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
public class InMemoryItemStorageImpl implements ItemStorage {

    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private Long idItems = 0L;

    @Override
    public Item addItem(Long userId, Item item) {
        item.setId(++idItems);
        item.setOwner(UserMapper.toUser(userService.getUserById(userId)));
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item updateItem = getItemById(itemId);

        if (!Objects.equals(updateItem.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("У пользователя нет доступа к этой вещи.");
        }

        ItemValidator.itemPatch(updateItem, item);


        return updateItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            List<Item> result = new ArrayList<>();

            for (Item i : items.values()) {
                if (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())
                        && i.getAvailable()) {
                    result.add(i);
                }
            }
            return result;
        }
    }

    @Override
    public Boolean deleteItem(Long itemId) {
        items.remove(itemId);
        return true;
    }

    @Override
    public Boolean isItemExists(Long id) {
        return items.containsKey(id);
    }
}