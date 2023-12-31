package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @GetMapping
    public List<ItemInfoDto> getAllItems(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту /items от User(Owner) c ID {} на получение списка всех своих Items.",
                userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getItemById(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Получен GET запрос по эндпоинту /items/{} от User c ID {} на получение Item с ID {}.", itemId, userId,
                itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USERID_HEADER) Long userId, @RequestParam String text) {
        log.info(
                "Получен GET запрос по эндпоинту /items/search от User c ID {} на получение списка Item по запросу '{}'.",
                userId, text);
        return itemService.searchItems(userId, text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USERID_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен POST запрос по эндпоинту /items от User(Owner) c ID {} на добавление Item {}.", userId,
                itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USERID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info(
                "Получен PATCH запрос по эндпоинту /items/{} от User(Owner) c ID {} на обновление данных Item с ID {}.",
                itemId, userId, itemId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Получен DELETE запрос по эндпоинту /items/{} от User(Owner) c ID {} на удаление Item с ID {}.", itemId,
                userId, itemId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USERID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
