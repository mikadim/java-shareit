package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIT {
    @Autowired
    private BookingServiceImpl bookingService;

    @Test
    @Sql("classpath:add_users.sql")
    @Sql("classpath:add_items.sql")
    @Sql("classpath:add_bookings.sql")
    void getUserBookings_whenItemOwnerHasNotBookings_thenReturnEmptyList() {
        Long userId = 2L;
        Integer from = 0;
        Integer size = 4;
        BookingStatusDto status = BookingStatusDto.ALL;

        Page<Booking> userBookings = bookingService.getUserBookings(userId, status, from, size);

        assertThat(userBookings.getContent().size(), equalTo(0));
    }

    @Test
    @Sql("classpath:add_users.sql")
    @Sql("classpath:add_items.sql")
    @Sql("classpath:add_bookings.sql")
    void getUserBookings_whenUserHasBookings_then() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 4;
        BookingStatusDto status = BookingStatusDto.ALL;

        Page<Booking> userBookings = bookingService.getUserBookings(userId, status, from, size);
        Long firstItemId = userBookings.getContent().get(0).getItem().getId();

        assertThat(userBookings.getContent().size(), equalTo(4));
        assertThat(firstItemId, equalTo(1L));
    }
}
