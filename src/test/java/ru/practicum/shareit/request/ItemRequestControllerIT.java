package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestServiceException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserRepositoryException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.request.ItemRequestController.REQUESTOR_ID_TAG;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;


    private ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "веник", 2L, LocalDateTime.now(), new ArrayList<>());
    private ItemForItemRequestDto itemForItemRequestDto = new ItemForItemRequestDto(3L, "веник", "обычный веник", Boolean.TRUE, 1L);
    private ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "совок", 2L, LocalDateTime.now(), new ArrayList<>());
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");

    @SneakyThrows
    @Test
    void getUserRequest_whenRequestorIdHeaderIsNotExist_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled() {
        Long requestId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequest(any(), any());
    }

    @ParameterizedTest(name = "{index}. requestorId = {arguments} ")
    @ValueSource(strings = {"", " ", "0", "-1"})
    @SneakyThrows
    void getUserRequest_whenRequestorIdIncorrect_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled(String requestorId) {
        Long requestId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequest(any(), any());
    }

    @ParameterizedTest(name = "{index}. requestId = {arguments} ")
    @ValueSource(strings = {"0", "-1"})
    @SneakyThrows
    void getUserRequest_whenRequestIdIncorrect_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled(String requestId) {
        Long requestorId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequest_whenRequestIdNotFound_thenStatusNotFoundAndItemRequestServiceMethodCalledOnlyOnce() {
        Long requestId = 99L;
        Long requestorId = 1L;
        when(itemRequestService.getRequest(requestorId, requestId))
                .thenThrow(new ItemRequestServiceException("не найден запрос с id: " + requestId));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("не найден запрос с id: " + requestId));

        verify(itemRequestService).getRequest(requestorId, requestId);
        verify(itemRequestService, times(1)).getRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequest_whenRequestorIdNotFound_thenStatusNotFoundAndItemRequestServiceMethodCalledOnlyOnce() {
        Long requestId = 1L;
        Long requestorId = 99L;
        when(itemRequestService.getRequest(requestorId, requestId))
                .thenThrow(new UserRepositoryException(requestorId + ": этот id не найден"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(requestorId + ": этот id не найден"));

        verify(itemRequestService).getRequest(requestorId, requestId);
        verify(itemRequestService, times(1)).getRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequest_whenItemRequestExistAndItemListIsEmpty_thenStatusOkAndItemRequestServiceMethodCalledOnlyOnce() {
        Long requestorId = 2L;
        Long requestId = 1L;

        when(itemRequestService.getRequest(requestorId, requestId))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(5)),
                        jsonPath("$.items.length()", is(0)),
                        jsonPath("$.id", is(itemRequestDto.getId()), Long.class),
                        jsonPath("$.description", is(itemRequestDto.getDescription())),
                        jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class),
                        jsonPath("$.created").value(itemRequestDto.getCreated().format(formatter)),
                        jsonPath("$.items", is(itemRequestDto.getItems())));

        verify(itemRequestService).getRequest(requestorId, requestId);
        verify(itemRequestService, times(1)).getRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequest_whenItemRequestExistWithItemList_thenStatusOkAndItemRequestServiceMethodCalledOnlyOnce() {
        Long requestorId = 2L;
        Long requestId = 1L;
        itemRequestDto.setItems(List.of(itemForItemRequestDto));
        when(itemRequestService.getRequest(requestorId, requestId))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(5)),
                        jsonPath("$.items.length()", is(1)),
                        jsonPath("$.id", is(itemRequestDto.getId()), Long.class),
                        jsonPath("$.description", is(itemRequestDto.getDescription())),
                        jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class),
                        jsonPath("$.created").value(itemRequestDto.getCreated().format(formatter)),
                        jsonPath("$.items[0].length()", is(5)),
                        jsonPath("$.items[0].id", is(itemForItemRequestDto.getId()), Long.class),
                        jsonPath("$.items[0].name", is(itemForItemRequestDto.getName())),
                        jsonPath("$.items[0].description", is(itemForItemRequestDto.getDescription())),
                        jsonPath("$.items[0].available", is(itemForItemRequestDto.getAvailable())),
                        jsonPath("$.items[0].requestId", is(itemForItemRequestDto.getRequestId()), Long.class));

        verify(itemRequestService).getRequest(requestorId, requestId);
        verify(itemRequestService, times(1)).getRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequests_whenItemsRequestsExistWithItemList_thenStatusOkAndItemRequestServiceMethodCalledOnlyOnce() {
        Long requestorId = 2L;
        itemRequestDto.setItems(List.of(itemForItemRequestDto));
        when(itemRequestService.getUserRequests(requestorId))
                .thenReturn(List.of(itemRequestDto, itemRequestDto2));

        mockMvc.perform(get("/requests")
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)),
                        jsonPath("$[0].items.length()", is(1)),
                        jsonPath("$[1].items.length()", is(0)));

        verify(itemRequestService).getUserRequests(requestorId);
        verify(itemRequestService, times(1)).getUserRequests(any());
    }

    @SneakyThrows
    @Test
    void createRequest_whenRequestorIdNotFound_thenStatusNotFoundAndItemRequestServiceMethodCalledOnlyOnce() {
        Long requestorId = 99L;
        when(itemRequestService.create(any(), any()))
                .thenThrow(new UserRepositoryException(requestorId + ": этот id не найден"));

        mockMvc.perform(post("/requests")
                        .content("{\"description\": \"веник\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(requestorId + ": этот id не найден"));

        verify(itemRequestService, times(1)).create(any(), any());
    }

    @SneakyThrows
    @Test
    void createRequest_whenCorrectRequest_thenStatusOk() {
        Long requestorId = 1L;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("веник");
        when(itemRequestService.create(dto, requestorId))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content("{\"description\": \"веник\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(5)),
                        jsonPath("$.items.length()", is(0)));

        verify(itemRequestService, times(1)).create(any(), any());
        verify(itemRequestService).create(dto, requestorId);
    }

    @SneakyThrows
    @Test
    void createRequest_whenDescriptionIsNull_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled() {
        Long requestorId = 99L;

        mockMvc.perform(post("/requests")
                        .content("{\"description\": null}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(any(), any());
    }

    @ParameterizedTest(name = "{index}. size = {arguments} ")
    @ValueSource(strings = {"0", "-1"})
    @SneakyThrows
    void getAllRequest_whenParamFromIncorrect_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled(String size) {
        Long requestorId = 2L;
        String from = "0";

        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAll(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequest_whenParamSizeIncorrect_thenStatusBadRequestAndItemRequestServiceMethodNeverCalled() {
        Long requestorId = 2L;
        String from = "-1";
        String size = "1";

        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAll(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequest_whenCorrectRequest_thenStatusOk() {
        Long requestorId = 2L;
        String from = "0";
        String size = "1";
        when(itemRequestService.getAll(requestorId, Integer.parseInt(from), Integer.parseInt(size)))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, requestorId)
                        .param("from", from)
                        .param("size", size))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(0)));

        verify(itemRequestService, times(1)).getAll(any(), any(), any());
        verify(itemRequestService).getAll(requestorId, Integer.parseInt(from), Integer.parseInt(size));
    }
}