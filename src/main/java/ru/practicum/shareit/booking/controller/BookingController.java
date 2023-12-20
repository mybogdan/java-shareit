package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final BookingServiceImpl bookingServiceImpl;

    @PostMapping
    public BookingInfoDto addBooking(@RequestHeader(USERID_HEADER) Long userId, @RequestBody BookingDto bookingDto) {
        log.info("Пришел POST запрос на добавление новой аренды {} от пользователя с id {}", bookingDto, userId);
        return bookingServiceImpl.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateBookingStatus(@RequestHeader(USERID_HEADER) Long userId,
                                              @PathVariable Long bookingId,
                                              @RequestParam boolean approved) {
        log.info("Пришел /PATCH запрос на принятие или отклонение аренды от пользователя с id {} к предмету с id {}",
                userId, bookingId);
        return bookingServiceImpl.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getCurrentBooking(@RequestHeader(USERID_HEADER) Long userId,
                                            @PathVariable Long bookingId) {
        log.info("Пришел /GET запрос на получение данных об аренде с id {} от пользователя {}", bookingId, userId);
        return bookingServiceImpl.getCurrentBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getBooking(@RequestHeader(USERID_HEADER) Long userId,
                                           @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для пользователя с id {}, и с параметром {}",
                userId, stateParam);
        return bookingServiceImpl.getBooking(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getAllByOwner(@RequestHeader(USERID_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для владельца с id {}, и с параметром {}",
                userId, stateParam);
        return bookingServiceImpl.getOwnerBooking(userId, stateParam, from, size);

    }
}
