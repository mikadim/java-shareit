package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.user.exception.UserRepositoryException;
import ru.practicum.shareit.user.exception.UserServiceException;

@Slf4j
@ControllerAdvice(assignableTypes = UserController.class)
class UserErrorHandler {

    @ExceptionHandler(UserRepositoryException.class)
    public ResponseEntity<String> repositoryHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<String> serviceHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
