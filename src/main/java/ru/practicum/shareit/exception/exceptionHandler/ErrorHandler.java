package ru.practicum.shareit.exception.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final ObjectNotFoundException e) {
        return Map.of("404 OBJECT NOT FOUND", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserAlreadyExists(final EntityAlreadyExist e) {
        return Map.of("409 ENTITY ALREADY EXISTS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final InvalidEntityException e) {
        return Map.of("400 BAD REQUEST", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownStateException(final UnknownBookingState e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getMessage());
    }
}