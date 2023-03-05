package ru.practicum.shareit.commonclasses;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.exception.BookingServiceException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.exception.UserRepositoryException;
import ru.practicum.shareit.user.exception.UserServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Map;


@Slf4j
@ControllerAdvice(assignableTypes = {ItemController.class, UserController.class, BookingController.class})
class ErrorHandler {

    @ExceptionHandler(UserRepositoryException.class)
    public ResponseEntity<String> userRepositoryHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        if (StringUtils.containsIgnoreCase(e.getMessage(), "ящик уже использовался")) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<String> userServiceHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemServiceException.class)
    public ResponseEntity<String> itemServiceHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemRepositoryException.class)
    public ResponseEntity<String> itemRepositoryHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> sqlHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BookingServiceException.class)
    public ResponseEntity<String> bookingServiceHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        if (StringUtils.containsIgnoreCase(e.getMessage(), "данные не доступны") ||
                StringUtils.containsIgnoreCase(e.getMessage(), "нет доступа для изменения статуса") ||
                StringUtils.containsIgnoreCase(e.getMessage(), "бронирование своих вещей запрещено"
                )) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> methodArgumentHandler(final MethodArgumentTypeMismatchException e) {
        log.debug(e.getMessage());
        if (StringUtils.containsIgnoreCase(e.getValue().toString(), "UNSUPPORTED_STATUS")) {
            return new ResponseEntity<>(Map.of("error", "Unknown state: " + e.getValue().toString()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

