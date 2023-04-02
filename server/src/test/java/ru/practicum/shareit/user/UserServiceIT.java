package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIT {
    private User user1;
    private User user2;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepositoryImpl userRepository;

    @BeforeEach
    private void addUser() {
        user1 = userRepository.create(new User(null, "test1@mail.ru", "test1Name"));
        user2 = userRepository.create(new User(null, "test2@mail.ru", "test2Name"));
    }

    @Test
    void update_whenCorrectValue_thenUserUpdated() {
        UserDto userDto = new UserDto(null, "new@mail.ru", "newName");
        UserDto updatedUserDto = userService.update(user1.getId(), userDto);

        assertThat(updatedUserDto.getEmail(), equalTo("new@mail.ru"));
        assertThat(updatedUserDto.getName(), equalTo("newName"));
    }

    @Test
    void update_whenEmailRepeated_thenException() {
        UserDto userDto = new UserDto(null, "test2@mail.ru", "test1Name");

        assertThrows(RuntimeException.class,
                () -> {
                    userService.update(user1.getId(), userDto);
                    userService.getAll();
                });
    }
}
