package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.ItemStatus;

import java.time.LocalDateTime;

@Data
public class BookingItemDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemStatus status;
}
