package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @NotNull @Valid UserDto dto) {
        return new ResponseEntity<>(userService.create(dto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId, @RequestBody @NotNull @Valid UserDto dto) {
        return new ResponseEntity<>(userService.update(userId, dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long userId) {
        return new ResponseEntity<>(userService.getById(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }
}
