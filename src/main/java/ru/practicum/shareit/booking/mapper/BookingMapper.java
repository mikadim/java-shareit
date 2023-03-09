package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;


@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "status", expression = "java(ru.practicum.shareit.item.model.ItemStatus.WAITING)")
    Booking toNewBooking(BookingDto dto);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingItemDto toBookingItemDto(Booking booking);
}
