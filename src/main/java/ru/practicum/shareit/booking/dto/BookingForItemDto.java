package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface BookingForItemDto {
    @Value("#{target.Id}")
    Long getId();
    @Value("#{target.booker.id}")
    Long getBookerId();
    @Value("#{target.start}")
    LocalDateTime getStart();
    @Value("#{target.end}")
    LocalDateTime getEnd();
}
