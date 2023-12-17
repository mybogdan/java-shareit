package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {

    List<ItemInfoDto> getAllItems(Long userId);

    ItemInfoDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(Long userId, String text);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
