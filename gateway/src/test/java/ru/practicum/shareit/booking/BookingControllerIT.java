package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.BookingController.BOOKER_ID_TAG;
import static ru.practicum.shareit.request.ItemRequestController.REQUESTOR_ID_TAG;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;


    @SneakyThrows
    @Test
    void getBookingStatus_whenBookerIdHeaderIsNotExist_thenStatusBadRequest() {
        Long bookingId = 1L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(any(), any());
    }

    @SneakyThrows
    @Test
    void changeBookingStatus_whenParamBookingStatusIsNotExist_thenBadRequestResponse() {
        Long ownerId = 1L;
        Long bookingId = 1L;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(BOOKER_ID_TAG, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).updateStatus(any(), any(), any());
    }


    @ParameterizedTest(name = "{index}. size = {arguments} ")
    @ValueSource(strings = {"0", "-1"})
    @SneakyThrows
    void getBookerBookings_whenParameterSizeIsInvalid_thenStatusBadRequest(String size) {
        Long userId = 1L;
        String from = "0";
        String state = "ALL";

        mockMvc.perform(get("/bookings")
                        .header(BOOKER_ID_TAG, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getBookerBookings_whenParameterFromIsInvalid_thenStatusBadRequest() {
        Long userId = 1L;
        String size = "2";
        String from = "-1";
        String state = "ALL";

        mockMvc.perform(get("/bookings")
                        .header(BOOKER_ID_TAG, userId)
                        .param("state", state.toString())
                        .param("from", from)
                        .param("size", size)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(any(), any(), any(), any());
    }


    @SneakyThrows
    @Test
    void crateBooking_whenStarInPast_thenStatusBadRequest() {
        Long bookerId = 1L;
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, bookerId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(bookerId, bookingDto);
    }

    @SneakyThrows
    @Test
    void crateBooking_whenStarLaterThenEnd_thenStatusBadRequest() {
        Long bookerId = 1L;
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, bookerId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(bookerId, bookingDto);
    }

    @SneakyThrows
    @Test
    void crateBooking_whenEndInPast_thenStatusBadRequest() {
        Long bookerId = 1L;
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(5), LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header(REQUESTOR_ID_TAG, bookerId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(bookerId, bookingDto);
    }
}