package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String USERID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Запрос на получение списка своих вещей.");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Запрос на получение вещи по id.");
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USERID_HEADER) Long userId, @RequestParam String text) {
        log.info("Запрос на поиск вещи.");
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USERID_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи.");
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USERID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на редактирование вещи.");
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public Boolean deleteItem(@PathVariable Long itemId) {
        log.info("Запрос на удаление вещи.");
        return itemService.deleteItem(itemId);
    }
}
