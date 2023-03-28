package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @SneakyThrows
    @DisplayName("Post запрос с некорректным body возвращает статус BadRequest и метод create не вызывается")
    @Test
    void createUser_whenDtoIsNull() {
        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @SneakyThrows
    @DisplayName("Email с некорректным форматом возвращает статус BadRequest и метод create не вызывается")
    @Test
    void createUser_whenEmailIncorrect() {
        UserDto newDto = new UserDto(3L, "abrakadabra", "user");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }
}