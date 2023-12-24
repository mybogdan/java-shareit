package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final String USER_NOT_FOUND = "Пользователь не найден.";
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public List<ItemRequestDto> getRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_NOT_FOUND));
        return itemRequestRepository.findAllByRequestorId(userId)
                .stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_NOT_FOUND));
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto, user);
        return this.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_NOT_FOUND));
        return this.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден.")));
    }

    @Override
    public List<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            log.info("Аргументы не могут быть отрицательными.");
            throw new InvalidEntityException("Аргументы не могут быть отрицательными.");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_NOT_FOUND));
        return itemRequestRepository
                .findAllByRequestorIdIsNot(userId, PageRequest.of((from / size), size, Sort.by("created")
                                .descending()))
                .stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    private ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(itemRequest.getRequestor().getId())
                .items(putItemDtoToRequest(itemRequest))
                .build();
    }

    private List<ItemDto> putItemDtoToRequest(ItemRequest itemRequest) {
        return itemRepository.findAllByRequest_Id(itemRequest.getId())
                .stream()
                .map(RequestMapper::toRequestItemDto)
                .collect(Collectors.toList());
    }
}