package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(Long userId, String text);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Boolean deleteItem(Long userId, Long itemId);
}
