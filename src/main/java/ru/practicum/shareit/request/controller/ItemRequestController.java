package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Получен GET запрос по эндпоинту /requests на получение всех ItemRequest с данными об ответах "
                + "на них для User с ID {}.", userId);
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USERID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получен GET запрос по эндпоинту /requests/{} на получение ItemRequest c ID {} для User с ID {}.",
                requestId, requestId, userId);
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequest(
            @RequestHeader(USERID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("Получен GET запрос по эндпоинту /requests/all на получение всех ItemRequest для User с ID {}.",
                userId);
        return itemRequestService.getAllRequest(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USERID_HEADER) Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST запрос по эндпоинту /requests на добавление нового ItemRequest {} от User с ID {}.",
                itemRequestDto, userId);
        return itemRequestService.addRequest(userId, itemRequestDto);
    }
}
