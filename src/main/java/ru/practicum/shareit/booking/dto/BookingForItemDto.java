package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Value;
import ru.practicum.shareit.item.model.ItemStatus;

import java.time.LocalDateTime;

public interface BookingForItemDto {
    @Value("#{target.id}")
    Long getId();

    @Value("#{target.booker.id}")
    Long getBookerId();

    @Value("#{target.start}")
    LocalDateTime getStart();

    @Value("#{target.end}")
    LocalDateTime getEnd();

    @Value("#{target.status}")
    ItemStatus getStatus();

}
