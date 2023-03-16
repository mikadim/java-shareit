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
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserRepositoryException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.item.ItemController.USER_ID_TAG;
import static ru.practicum.shareit.item.model.ItemStatus.WAITING;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    BookingItemDto lastBooking = new BookingItemDto(1L, 2L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), WAITING);
    BookingItemDto nextBooking = new BookingItemDto(2L, 2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), WAITING);
    CommentDto comment = new CommentDto(1L, "test comment", "test", LocalDateTime.now());
    ItemDto itemDto = new ItemDto(1L, "test", "super test", Boolean.TRUE, 2L, lastBooking
            , nextBooking, List.of(comment), 2L);
    ItemDto itemDto2 = new ItemDto(2L, "test2", "super test2", Boolean.TRUE, null, null
            , null, null, null);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");

    @SneakyThrows
    @Test
    void getItem_whenRequestorIdHeaderIsNotExist_thenStatusBadRequest() {
        Long itemId = 1L;
        mockMvc.perform(get("/items/{id}", itemId)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getById(any(), any());
    }

    @SneakyThrows
    @Test
    void getItem_whenItemIdNotFound_thenStatusNotFound() {
        Long itemId = 99L;
        Long userId = 1L;
        when(itemService.getById(itemId, userId))
                .thenThrow(new UserRepositoryException(itemId + ": этот id не найден"));

        mockMvc.perform(get("/items/{id}", itemId)
                        .header(USER_ID_TAG, userId)
                        .accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andExpect(content().string(itemId + ": этот id не найден"));

        verify(itemService, times(1)).getById(any(), any());
    }

    @SneakyThrows
    @Test
    void getItem_whenCorrectRequest_thenStatusOkAndItemServiceMethodCalledOnlyOnce() {
        Long itemId = 1L;
        Long userId = 1L;
        when(itemService.getById(itemId, userId))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header(USER_ID_TAG, userId)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(9)),
                        jsonPath("$.lastBooking.length()", is(5)),
                        jsonPath("$.nextBooking.length()", is(5)),
                        jsonPath("$.comments.length()", is(1)),
                        jsonPath("$.id", is(itemDto.getId()), Long.class),
                        jsonPath("$.name", is(itemDto.getName())),
                        jsonPath("$.description", is(itemDto.getDescription())),
                        jsonPath("$.available", is(itemDto.getAvailable())),
                        jsonPath("$.request", is(itemDto.getRequestId()), Long.class),
                        jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class),
                        jsonPath("$.lastBooking.bookerId", is(itemDto.getLastBooking().getBookerId()), Long.class),
                        jsonPath("$.lastBooking.start").value(itemDto.getLastBooking().getStart().format(formatter)),
                        jsonPath("$.lastBooking.end").value(itemDto.getLastBooking().getEnd().format(formatter)),
                        jsonPath("$.lastBooking.status", is(itemDto.getLastBooking().getStatus().toString())),
                        jsonPath("$.comments[0].id").value(itemDto.getComments().get(0).getId()),
                        jsonPath("$.comments[0].text").value(itemDto.getComments().get(0).getText()),
                        jsonPath("$.comments[0].authorName").value(itemDto.getComments().get(0).getAuthorName()),
                        jsonPath("$.comments[0].created").value(itemDto.getComments().get(0).getCreated().format(formatter)),
                        jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).getById(any(), any());
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

        verify(itemService, never()).getByUserId(any(), any(), any());
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

        verify(itemService, never()).getByUserId(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getItems_whenCorrectRequest_thenStatusOkAndItemServiceMethodCalledOnlyOnce() {
        Long userId = 1L;
        Integer size = 2;
        Integer from = 0;
        when(itemService.getByUserId(userId, from, size)).thenReturn(List.of(itemDto, itemDto2));

        mockMvc.perform(get("/items")
                        .header(USER_ID_TAG, userId)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)));

        verify(itemService, times(1)).getByUserId(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void searchItems_whenCorrectRequest_thenStatusOkAndItemServiceMethodCalledOnlyOnce() {
        Integer size = 2;
        Integer from = 0;
        String text = "test";
        when(itemService.getByText(text, from, size)).thenReturn(List.of(itemDto, itemDto2));


        mockMvc.perform(get("/items/search")
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .param("text", text)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)));

        verify(itemService, times(1)).getByText(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void searchItems_whenCorrectRequestWithoutFromAndSize_thenStatusOkAndItemServiceMethodCalledOnlyOnce() {
        String text = "test";
        when(itemService.getByText(text, null, null)).thenReturn(List.of(itemDto, itemDto2));

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)));

        verify(itemService, times(1)).getByText(any(), any(), any());
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

        verify(itemService, never()).createComment(any(), any(), any());
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

        verify(itemService, never()).createComment(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void createComment_whenCorrectObject_thenStatusIsOkAndItemServiceMethodCalledOnlyOnce() {
        Long authorId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto(null, "testText", "testUser", null);
        when(itemService.createComment(authorId, itemId, commentDto)).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(USER_ID_TAG, authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4)));

        verify(itemService, times(1)).createComment(any(), any(), any());
    }


    @SneakyThrows
    @Test
    void createItem_whenCorrectRequest_thenStatusOk() {
        Long authorId = 1L;
        ItemDto newDto = new ItemDto(null, "testName", "testDescription", Boolean.TRUE, null, null, null, null, null);
        when(itemService.create(authorId, newDto))
                .thenReturn(newDto);

        mockMvc.perform(post("/items")
                        .content("{\"name\": \"testName\", \"description\": \"testDescription\", \"available\": \"true\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(USER_ID_TAG, authorId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(9)));

        verify(itemService, times(1)).create(any(), any());
    }

    @SneakyThrows
    @Test
    void updateItem_whenCorrectRequest_thenStatusOk() {
        Long authorId = 1L;
        Long itemId = 1L;
        ItemDto newDto = new ItemDto(null, null, null, Boolean.FALSE, null, null, null, null, null);
        when(itemService.update(authorId, authorId, newDto))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content("{\"available\": \"false\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(USER_ID_TAG, authorId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(9)));

        verify(itemService, times(1)).update(any(), any(), any());
    }
}