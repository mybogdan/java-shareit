package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

public class BookingValidator {

    public static Boolean bookingValidate(BookingDto entity) {
        return (!entity.getStart().equals(entity.getEnd())) &&
                (!entity.getEnd().isBefore(entity.getStart())) &&
                (!entity.getStart().isBefore(LocalDateTime.now()) &&
                !entity.getEnd().isBefore(LocalDateTime.now()));
    }
}
