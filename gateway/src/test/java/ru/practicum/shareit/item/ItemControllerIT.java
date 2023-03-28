package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.item.ItemController.USER_ID_TAG;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;


    @SneakyThrows
    @Test
    void getItem_whenRequestorIdHeaderIsNotExist_thenStatusBadRequest() {
        Long itemId = 1L;
        mockMvc.perform(get("/items/{id}", itemId)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItem(any(), any());
    }

    @ParameterizedTest(name = "{index}. size = {arguments} ")
    @ValueSource(strings = {"0", "-1"})
    @SneakyThrows
    void getItems_whenParameterSizeIsInvalid_thenStatusBadRequest(String size) {
        Long userId = 1L;
        String from = "0";

        mockMvc.perform(get("/items")
                        .header(USER_ID_TAG, userId)
                        .param("from", from)
                        .param("size", size)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getItems_whenParameterFromIsInvalid_thenStatusBadRequest() {
        Long userId = 1L;
        String size = "2";
        String from = "-1";

        mockMvc.perform(get("/items")
                        .header(USER_ID_TAG, userId)
                        .param("from", from)
                        .param("size", size)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(any(), any(), any());
    }

    @ParameterizedTest(name = "{index}. text = {arguments} ")
    @ValueSource(strings = {"", " "})
    @SneakyThrows
    void createComment_whenIncorrectObject_thenStatusIsBadRequest(String text) {
        Long authorId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto(2L, text, "testUser", LocalDateTime.now());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(USER_ID_TAG, authorId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createComment_whenObjectIsNotExist_thenStatusIsBadRequest() {
        Long authorId = 1L;
        Long itemId = 1L;

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(USER_ID_TAG, authorId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(any(), any(), any());
    }
}