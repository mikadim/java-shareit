package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserRepositoryException;
import ru.practicum.shareit.user.exception.UserServiceException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto = new UserDto(1L, "user@mail.ru", "testUser");
    private UserDto userDto2 = new UserDto(2L, "user2@mail.ru", "testUser2");

    @SneakyThrows
    @Test
    void getUser_whenUserIsNotExist_thenStatusNotFoundWithException() {
        Long userId = 99L;
        when(userService.getById(userId))
                .thenThrow(new UserRepositoryException(userId + ": этот id не найден"));

        mockMvc.perform(get("/users/{id}", userId)
                        .accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andExpect(content().string(userId + ": этот id не найден"));

        verify(userService).getById(userId);
        verify(userService, times(1)).getById(any());
    }

    @SneakyThrows
    @Test
    void getUser_whenUserExist_thenStatusOk() {
        Long userId = 1L;
        when(userService.getById(userId))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(3)),
                        jsonPath("$.id", is(userDto.getId()), Long.class),
                        jsonPath("$.email", is(userDto.getEmail())),
                        jsonPath("$.name", is(userDto.getName())));

        verify(userService).getById(userId);
        verify(userService, times(1)).getById(any());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenDelete_thenStatusOk() {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk());

        verify(userService).deleteById(userId);
        verify(userService, times(1)).deleteById(any());
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenRequestExistsUsers_thenStatusOkAndJsonWithUsers() {
        when(userService.getAll())
                .thenReturn(List.of(userDto, userDto2));

        mockMvc.perform(get("/users")
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)),
                        jsonPath("$[0].length()", is(3)),
                        jsonPath("$[1].length()", is(3)));

        verify(userService, times(1)).getAll();
    }

    @SneakyThrows
    @Test
    void createUser_whenDtoIsNull_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled() {
        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailIncorrect_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled() {
        UserDto newDto = new UserDto(3L, "abrakadabra", "user");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenAttributesIncorrect_thenStatusBadRequest() {
        when(userService.create(any()))
                .thenThrow(new UserServiceException("поля name и/или description не заполнены"));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("поля name и/или description не заполнены"));
    }

    @SneakyThrows
    @Test
    void createUser_whenCorrectRequest_thenStatusOk() {
        UserDto newDto = new UserDto(null, "user@mail.ru", "testUser");
        when(userService.create(newDto))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content("{\"name\": \"testUser\", \"email\": \"user@mail.ru\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(3)));

        verify(userService, times(1)).create(any());
        verify(userService).create(newDto);
    }

    @SneakyThrows
    @Test
    void updateUserUser_whenCorrectRequest_thenStatusOk() {
        Long userId = 1L;
        UserDto newDto = new UserDto(null, null, "superTest");
        UserDto userDto3 = new UserDto(userId, "test@mail.ru", "superTest");
        when(userService.update(userId, newDto))
                .thenReturn(userDto3);

        mockMvc.perform(patch("/users/{id}", userId)
                        .content("{\"name\": \"superTest\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(3)),
                        jsonPath("$.id", is(userDto3.getId()), Long.class),
                        jsonPath("$.email", is(userDto3.getEmail())),
                        jsonPath("$.name", is(userDto3.getName())));

        verify(userService, times(1)).update(any(), any());
        verify(userService).update(userId, newDto);
    }

}