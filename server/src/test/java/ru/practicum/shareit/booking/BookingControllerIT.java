package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.exception.BookingServiceException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
import static ru.practicum.shareit.booking.BookingController.BOOKER_ID_TAG;
import static ru.practicum.shareit.item.model.ItemStatus.APPROVED;
import static ru.practicum.shareit.request.ItemRequestController.REQUESTOR_ID_TAG;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Item item = new Item(2L, "пылесос", "хороший пылесос", Boolean.TRUE, 2L, 1L);
    private User booker = new User(1L, "test@mail.ru", "test");
    private Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, APPROVED);
    private Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), item, booker, APPROVED);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @SneakyThrows
    @DisplayName("Когда владелец брони не найден, статус NotFound и метод getBooking вызывается один раз")
    @Test
    void getUserRequest_whenRequestorIdNotFound() {
        Long bookingId = 1L;
        Long bookerId = 99L;
        when(bookingService.getBooking(bookerId, bookingId))
                .thenThrow(new BookingServiceException("данные не доступны"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .accept(MediaType.ALL)
                        .header(BOOKER_ID_TAG, bookerId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("данные не доступны"));

        verify(bookingService, times(1)).getBooking(any(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequest_whenCorrectRequest_thenStatusOk() {
        Long bookingId = 1L;
        Long bookerId = 1L;
        when(bookingService.getBooking(bookerId, bookingId))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .accept(MediaType.ALL)
                        .header(BOOKER_ID_TAG, bookerId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(6)),
                        jsonPath("$.item.length()", is(6)),
                        jsonPath("$.booker.length()", is(3)),
                        jsonPath("$.id", is(booking.getId()), Long.class),
                        jsonPath("$.start").value(booking.getStart().format(formatter)),
                        jsonPath("$.end").value(booking.getEnd().format(formatter)),
                        jsonPath("$.status", is(booking.getStatus().toString())),
                        jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class),
                        jsonPath("$.booker.email", is(booking.getBooker().getEmail())),
                        jsonPath("$.booker.name", is(booking.getBooker().getName())));

        verify(bookingService).getBooking(bookerId, bookingId);
        verify(bookingService, times(1)).getBooking(any(), any());
    }

    @SneakyThrows
    @DisplayName("Корректный запрос возвращает статус ОК и метод updateStatus вызывается один раз")
    @Test
    void changeBookingStatus_whenCorrectRequest() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Boolean status = Boolean.TRUE;
        when(bookingService.updateStatus(ownerId, bookingId, status)).thenReturn(booking);

        verify(bookingService, never()).updateStatus(any(), any(), any());
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(BOOKER_ID_TAG, ownerId)
                        .param("approved", status.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(6)),
                        jsonPath("$.item.length()", is(6)),
                        jsonPath("$.booker.length()", is(3)));

        verify(bookingService, times(1)).updateStatus(any(), any(), any());
    }

    @SneakyThrows
    @DisplayName("Корректный запрос возвращает статус ОК и метод getBookerBookings вызывается один раз")
    @Test
    void getBookerBookings_whenCorrectRequest() {
        Long userId = 1L;
        Integer size = 2;
        Integer from = 0;
        BookingStatusDto state = BookingStatusDto.ALL;
        when(bookingService.getBookerBookings(userId, state, from, size)).thenReturn(new PageImpl<>(List.of(booking, booking2)));

        mockMvc.perform(get("/bookings")
                        .header(BOOKER_ID_TAG, userId)
                        .param("state", state.toString())
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)));

        verify(bookingService, times(1)).getBookerBookings(any(), any(), any(), any());
    }

    @SneakyThrows
    @DisplayName("Корректный запрос возвращает статус ОК и метод getUserBookings вызывается один раз")
    @Test
    void getUserBookings_whenCorrectRequest() {
        Long userId = 1L;
        Integer size = 2;
        Integer from = 0;
        BookingStatusDto state = BookingStatusDto.ALL;
        when(bookingService.getUserBookings(userId, state, from, size)).thenReturn(new PageImpl<>(List.of(booking, booking2)));

        mockMvc.perform(get("/bookings/owner")
                        .header(BOOKER_ID_TAG, userId)
                        .param("state", state.toString())
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .accept(MediaType.ALL))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)));

        verify(bookingService, times(1)).getUserBookings(any(), any(), any(), any());
    }

    @SneakyThrows
    @DisplayName("Корректный запрос возвращает статус ОК и метод create вызывается один раз")
    @Test
    void crateBooking_whenCorrectRequest() {
        Long bookerId = 1L;
        LocalDateTime start = LocalDateTime.parse(booking2.getStart().format(formatter));
        LocalDateTime end = LocalDateTime.parse(booking2.getEnd().format(formatter));
        BookingDto bookingDto = new BookingDto(1L, start, end);
        when(bookingService.create(bookingDto, bookerId)).thenReturn(booking2);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, bookerId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(6)),
                        jsonPath("$.item.length()", is(6)),
                        jsonPath("$.booker.length()", is(3)),
                        jsonPath("$.id", is(booking2.getId()), Long.class),
                        jsonPath("$.start").value(booking2.getStart().format(formatter)),
                        jsonPath("$.end").value(booking2.getEnd().format(formatter)),
                        jsonPath("$.status", is(booking2.getStatus().toString())),
                        jsonPath("$.booker.id", is(booking2.getBooker().getId()), Long.class),
                        jsonPath("$.booker.email", is(booking2.getBooker().getEmail())),
                        jsonPath("$.booker.name", is(booking2.getBooker().getName())));

        verify(bookingService, times(1)).create(bookingDto, bookerId);
    }
}