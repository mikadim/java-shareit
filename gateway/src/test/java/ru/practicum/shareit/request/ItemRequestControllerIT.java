package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.request.ItemRequestController.REQUESTOR_ID_TAG;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @SneakyThrows
    @DisplayName("Запрос с отсутствующим заголовком REQUESTOR_ID_TAG возвращает статус BadRequest и метод getRequest не вызывается")
    @Test
    void getUserRequest_whenRequestorIdHeaderIsNotExist() {
        Long requestId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequest(any(), any());
    }

    @ParameterizedTest(name = "{index}. requestorId = {arguments} ")
    @ValueSource(strings = {"", " ", "0", "-1"})
    @DisplayName("Некорректный id автора запроса возвращает статус BadRequest и метод getRequest не вызывается")
    @SneakyThrows
    void getUserRequest_whenRequestorIdIncorrect(String requestorId) {
        Long requestId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequest(any(), any());
    }

    @ParameterizedTest(name = "{index}. requestId = {arguments} ")
    @ValueSource(strings = {"0", "-1"})
    @DisplayName("Некорректный id запроса возвращает статус BadRequest и метод getRequest не вызывается")
    @SneakyThrows
    void getUserRequest_whenRequestIdIncorrect(String requestId) {
        Long requestorId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequest(any(), any());
    }

    @SneakyThrows
    @DisplayName("При пустом описании возвращается статус BadRequest и метод create не вызывается")
    @Test
    void createRequest_whenDescriptionIsNull() {
        Long requestorId = 99L;

        mockMvc.perform(post("/requests")
                        .content("{\"description\": null}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createRequest(any(), any());
    }

    @ParameterizedTest(name = "{index}. size = {arguments} ")
    @ValueSource(strings = {"0", "-1"})
    @DisplayName("При некорректном параметре пагинации size возвращается BadRequest и метод getAll не вызывается")
    @SneakyThrows
    void getAllRequest_whenParamFromIncorrect(String size) {
        Long requestorId = 2L;
        String from = "0";

        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAll(any(), any(), any());
    }

    @SneakyThrows
    @DisplayName("При некорректном параметре пагинации from возвращается BadRequest и метод getAll не вызывается")
    @Test
    void getAllRequest_whenParamSizeIncorrect() {
        Long requestorId = 2L;
        String from = "-1";
        String size = "1";

        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAll(any(), any(), any());
    }
}