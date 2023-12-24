package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getRequests(Long userId);

    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size);
}
