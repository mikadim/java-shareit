package ru.practicum.shareit.commonclasses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.request.ItemRequestController;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@ControllerAdvice(assignableTypes = {BookingController.class, ItemRequestController.class})
class ErrorHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> methodArgumentHandler(final IllegalArgumentException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
