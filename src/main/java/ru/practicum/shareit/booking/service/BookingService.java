package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import javax.transaction.Transactional;
import java.util.List;

public interface BookingService {
    @Transactional
    BookingInfoDto addBooking(Long userId, BookingDto bookingDto);

    BookingInfoDto updateBookingStatus(Long userId, Long bookingId, boolean approved);

    BookingInfoDto getCurrentBooking(Long userId, Long bookingId);

    List<BookingInfoDto> getBooking(Long userId, String stateParam);

    List<BookingInfoDto> getOwnerBooking(Long userId, String stateParam);
}
