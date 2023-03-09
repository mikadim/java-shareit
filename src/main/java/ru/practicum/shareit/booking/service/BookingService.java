package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto dto, Long bookerId);

    Booking updateStatus(Long ownerId, Long bookingId, Boolean status);

    Booking getStatus(Long userId, Long bookingId);

    List<Booking> getBookerBookings(Long userId, BookingStatusDto status);

    List<Booking> getUserBookings(Long userId, BookingStatusDto status);
}
