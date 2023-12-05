package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item toItem);

    Item getItemById(Long userId, Long itemId);

    List<Item> getAllItems(Long userId);

    List<Item> searchItems(Long userId, String text);

    Boolean deleteItem(Long userId, Long itemId);

    Boolean isItemExists(Long id);
}
