package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final BookingServiceImpl bookingServiceImpl;

    @Autowired
    public BookingController(BookingServiceImpl bookingServiceImpl) {
        this.bookingServiceImpl = bookingServiceImpl;
    }

    @PostMapping
    public BookingInfoDto addBooking(@RequestHeader(USERID_HEADER) Long userId, @Valid @RequestBody BookingDto bookingDto) {
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
    public BookingInfoDto getById(@RequestHeader(USERID_HEADER) Long userId,
                                  @PathVariable Long bookingId) {
        log.info("Пришел /GET запрос на получение данных об аренде с id {} от пользователя {}", bookingId, userId);
        return bookingServiceImpl.getCurrentBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getAllByBooker(@RequestHeader(USERID_HEADER) Long userId,
                                               @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для пользователя с id {}, и с параметром {}",
                userId, stateParam);
        return bookingServiceImpl.getBooking(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getAllByOwner(@RequestHeader(USERID_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для владельца с id {}, и с параметром {}",
                userId, stateParam);
        return bookingServiceImpl.getOwnerBooking(userId, stateParam);
    }
}
