package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.ItemStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static ru.practicum.shareit.booking.dto.BookingStatusDto.*;
import static ru.practicum.shareit.item.model.ItemStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class BookingUT {

    @Test
    void getBookings() {
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1), null, null, APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(4), null, null, APPROVED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), null, null, APPROVED);
        Booking booking4 = new Booking(4L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), null, null, APPROVED);
        Booking booking5 = new Booking(5L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), null, null, ItemStatus.REJECTED);
        Booking booking6 = new Booking(6L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), null, null, ItemStatus.WAITING);
        List<Booking> bookings = List.of(booking1, booking2, booking3, booking4, booking5, booking6);

        List<Booking> getBookings1 = ReflectionTestUtils.invokeMethod(BookingServiceImpl.class, "getBookings", CURRENT, bookings);
        List<Booking> getBookings2 = ReflectionTestUtils.invokeMethod(BookingServiceImpl.class, "getBookings", PAST, bookings);
        List<Booking> getBookings3 = ReflectionTestUtils.invokeMethod(BookingServiceImpl.class, "getBookings", FUTURE, bookings);
        List<Booking> getBookings4 = ReflectionTestUtils.invokeMethod(BookingServiceImpl.class, "getBookings", BookingStatusDto.REJECTED, bookings);
        List<Booking> getBookings5 = ReflectionTestUtils.invokeMethod(BookingServiceImpl.class, "getBookings", BookingStatusDto.WAITING, bookings);
        List<Booking> getBookings6 = ReflectionTestUtils.invokeMethod(BookingServiceImpl.class, "getBookings", ALL, bookings);

        assertThat(getBookings1.size(), equalTo(2));
        assertThat(getBookings1.get(0).getStart(), greaterThan(getBookings1.get(1).getStart()));
        assertThat(getBookings2.size(), equalTo(2));
        assertThat(getBookings1.get(0).getStart(), greaterThan(getBookings1.get(1).getStart()));
        assertThat(getBookings3.size(), equalTo(2));
        assertThat(getBookings1.get(0).getStart(), greaterThan(getBookings1.get(1).getStart()));
        assertThat(getBookings4.size(), equalTo(1));
        assertThat(getBookings4.get(0).getId(), equalTo(5L));
        assertThat(getBookings5.size(), equalTo(1));
        assertThat(getBookings5.get(0).getId(), equalTo(6L));
        assertThat(getBookings6.size(), equalTo(6));
    }
}
