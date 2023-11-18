package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.validator.ItemValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemStorage.getAllItems(userId);
        return ItemMapper.toItemsDto(items);
    }

    public ItemDto getItemById(Long userId, Long itemId) {
        if (!itemStorage.isItemExists(itemId)) {
            throw new ObjectNotFoundException("Такой вещи не существует.");
        }
        Item item = itemStorage.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemStorage.searchItems(text);
        return ItemMapper.toItemsDto(items);
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (!userStorage.isUserExistsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        if (ItemValidator.itemCheck(itemDto)) {
            throw new InvalidEntityException("Отсутствует информация в теле вещи.");
        }
        Item item = itemStorage.addItem(userId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (!itemStorage.isItemExists(itemId)) {
            throw new ObjectNotFoundException("Такой вещи не существует.");
        }

        Item item = itemStorage.updateItem(userId, itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    public Boolean deleteItem(Long itemId) {
        if (!itemStorage.isItemExists(itemId)) {
            throw new ObjectNotFoundException("Такой вещи не существует.");
        }
        return itemStorage.deleteItem(itemId);
    }
}
