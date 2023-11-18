package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item toItem);

    Item getItemById(Long itemId);

    List<Item> getAllItems(Long userId);

    List<Item> searchItems(String text);

    Boolean deleteItem(Long itemId);

    Boolean isItemExists(Long id);
}
