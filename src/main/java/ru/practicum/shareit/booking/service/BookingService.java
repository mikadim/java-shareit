package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    Booking create(BookingDto dto, Long bookerId);

    Booking updateStatus(Long ownerId, Long bookingId, Boolean status);

    Booking getBooking(Long userId, Long bookingId);

    Page<Booking> getBookerBookings(Long userId, BookingStatusDto status, Integer from, Integer size);

    Page<Booking> getUserBookings(Long userId, BookingStatusDto status, Integer from, Integer size);
}
