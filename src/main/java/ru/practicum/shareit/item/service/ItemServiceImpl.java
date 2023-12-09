package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemInfoDto> getAllItems(Long userId) {
        return repository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemInfo)
                .peek(this::setBookingToItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemInfoDto getItemById(Long userId, Long itemId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой вещи не существует."));

        ItemInfoDto itemInfoDto = ItemMapper.toItemInfo(item);

        if (item.getOwner().getId().equals(userId)) {
            setBookingToItem(itemInfoDto);
        }

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemInfoDto.setComments(comments.isEmpty() ? Collections.emptyList() : comments);

        return itemInfoDto;
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return repository.search(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден.")));
        return ItemMapper.toItemDto(repository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Такой вещи не существует."));
        if (!Objects.equals(updatedItem.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("Товар не принадлежит этому пользователю.");
        }

        updatedItem = itemUpdate(updatedItem, itemDto);
        return ItemMapper.toItemDto(repository.save(updatedItem));
    }

    @Transactional
    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (!repository.existsById(itemId)) {
            throw new ObjectNotFoundException("Такой вещи не существует.");
        }
        repository.deleteById(itemId);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        validateComment(userId, itemId, commentDto);
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(repository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Такой вещи не существует.")));
        comment.setUser(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь не найден.")));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void validateComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new InvalidEntityException("Неверный текст комментария.");
        }
        if (!isAlreadyBooked(userId, itemId)) {
            throw new InvalidEntityException("Пользователь уже держит элемент.");
        }
        if (isOwner(userId, itemId)) {
            throw new InvalidEntityException("Пользователь является владельцем.");
        }
    }

    private Boolean isAlreadyBooked(Long userId, Long itemId) {
        List<Booking> bookingList = bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(userId, itemId);
        if (bookingList.isEmpty()) {
            throw new InvalidEntityException("Пользователь не забронировал товар.");
        }
        return bookingList.stream()
                .anyMatch(booking ->
                        booking.getEnd().isBefore(LocalDateTime.now()));
    }

    private Boolean isOwner(Long userId, Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Пользователь не найден.")).getOwner().getId().equals(userId);
    }

    private Item itemUpdate(Item updatedItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            updatedItem.setOwner(userRepository.findById(itemDto.getOwner().getId())
                    .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден.")));
        }
        if (itemDto.getRequest() != null) {
            updatedItem.setRequest(new ItemRequest());
        }
        return updatedItem;
    }

    private void setBookingToItem(ItemInfoDto item) {
        List<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());
        if (!bookingList.isEmpty()) {
            Booking lastBooking = bookingList.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getStart().isBefore(LocalDateTime.now()))
                    .reduce((booking, booking2) -> booking2)
                    .orElse(null);
            if (lastBooking != null) {
                item.setLastBooking(BookingMapper.toBookingItem(lastBooking));
            }
            Booking nextBooking = bookingList.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null);
            if (nextBooking != null) {
                item.setNextBooking(BookingMapper.toBookingItem(nextBooking));
            }
        }
    }
}
