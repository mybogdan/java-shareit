package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemStorage.getAllItems(userId);
        return ItemMapper.toItemsDto(items);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        if (!itemStorage.isItemExists(itemId)) {
            log.info("Item с ID {} не найден.", itemId);
            throw new EntityNotFoundException("Такой вещи не существует.");
        }
        Item item = itemStorage.getItemById(userId, itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        List<Item> items = itemStorage.searchItems(userId, text);
        return ItemMapper.toItemsDto(items);
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (!userStorage.userExistValidation(userId)) {
            log.info("User с ID {} не найден .", userId);
            throw new EntityNotFoundException("Пользователь не найден");
        }
        validationItem(itemDto);
        Item item = itemStorage.addItem(userId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item oldItem = itemStorage.getItemById(userId, itemId);

        if (!itemStorage.isItemExists(itemId)) {
            log.info("Item с ID {} не найден.", itemId);
            throw new EntityNotFoundException("Такой вещи не существует.");
        }

        if (!Objects.equals(oldItem.getOwner().getId(), userId)) {
            log.info("User с ID {} не является owner.", userId);
            throw new ResponseStatusException(HttpStatus.valueOf(404), "User с ID {} не является owner.");
        }

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemStorage.updateItem(userId, itemId, oldItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public Boolean deleteItem(Long userId, Long itemId) {
        if (!itemStorage.isItemExists(itemId)) {
            log.info("Item с ID {} не найден.", itemId);
            throw new EntityNotFoundException("Такой вещи не существует.");
        }
        return itemStorage.deleteItem(userId, itemId);
    }

    private void validationItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Поле Available не может быть пустым");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Поле Name не может быть пустым");
        }
        if (itemDto.getDescription() == null
                || itemDto.getDescription().isEmpty()
                || itemDto.getDescription().isBlank()
        ) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Поле Description не может быть пустым");
        }
    }

}
