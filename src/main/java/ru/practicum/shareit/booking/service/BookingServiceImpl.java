package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnknownBookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final String USER_ERROR = "Пользователь не найден.";
    private static final String ITEM_ERROR = "Такой вещи не существует.";
    private static final String BOOKING_ERROR = "Бронирование не найдено.";
    private static final String BOOKING_STATE_ERROR = "Неизвестный статус бронирования.";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingInfoDto addBooking(Long userId, BookingDto bookingDto) {
        if (!BookingValidator.isBookingTimeIntervalValid(bookingDto)) {
            log.info("BookingDto недействителен.");
            throw new InvalidEntityException("BookingDto недействителен.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException(ITEM_ERROR));

        if (!item.getAvailable()) {
            log.info(ITEM_ERROR);
            throw new InvalidEntityException(ITEM_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_ERROR));

        if (user.getId().equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException(USER_ERROR);
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingInfoDto(bookingRepository.save(booking));
    }

    @Override
    public BookingInfoDto updateBookingStatus(Long userId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException(BOOKING_ERROR));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_ERROR));

        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            log.info("Невозможно подтвердить бронирование. Пользователь не является владельцем этого объекта.");
            throw new ObjectNotFoundException("Невозможно подтвердить бронирование. Пользователь не является владельцем этого объекта.");
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (booking.getStatus() == bookingStatus) {
            log.info("Статус: " + bookingStatus);
            throw new InvalidEntityException("Статус: " + bookingStatus);
        }

        booking.setStatus(bookingStatus);

        return BookingMapper.toBookingInfoDto(bookingRepository.save(booking));
    }


    @Override
    public BookingInfoDto getCurrentBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException(BOOKING_ERROR));

        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
           log.info("Этот пользователь не является владельцем объекта.");
            throw new ObjectNotFoundException("Этот пользователь не является владельцем объекта.");
        }

        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public List<BookingInfoDto> getBooking(Long userId, String stateParam, Integer from, Integer size) {

        if (from < 0 || size < 0) {
            throw new InvalidEntityException("Аргумент имеет отрицательное значение.");
        }

        BookingState bookingState = checkState(stateParam);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime dateTimeNow = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_ERROR));

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), PageRequest.of((from / size), size));
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndIsBefore(user.getId(), dateTimeNow, sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsAfter(user.getId(), dateTimeNow, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(user.getId(), dateTimeNow, dateTimeNow, sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(user.getId(), BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(user.getId(), BookingStatus.REJECTED);
                break;
        }

        return bookingList.isEmpty() ? Collections.emptyList() : bookingList.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingInfoDto> getOwnerBooking(Long userId, String stateParam, Integer from, Integer size) {

        if (from < 0 || size < 0) {
            throw new InvalidEntityException("Аргумент имеет отрицательное значение.");
        }

        BookingState bookingState = checkState(stateParam);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime dateTimeNow = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER_ERROR));

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(user.getId(), PageRequest.of((from / size), size));
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(user.getId(), dateTimeNow, sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(user.getId(), dateTimeNow, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(user.getId(), dateTimeNow, dateTimeNow, sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStatus(user.getId(), BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStatus(user.getId(), BookingStatus.REJECTED);
                break;
        }

        return bookingList.isEmpty() ? Collections.emptyList() : bookingList.stream()
                .map(BookingMapper::toBookingInfoDto)
                .collect(Collectors.toList());
    }

    private BookingState checkState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException exception) {
            log.info(String.format("Unknown state: %s", state));
            throw new UnknownBookingState(String.format("Unknown state: %s", state));
        }
    }
}