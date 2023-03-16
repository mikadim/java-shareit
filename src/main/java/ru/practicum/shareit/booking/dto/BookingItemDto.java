package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.ItemStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class BookingItemDto {
    private Long id;
    private Long bookerId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
    private ItemStatus status;
}
