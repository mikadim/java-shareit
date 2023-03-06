package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.validator.BookingDtoValid;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@BookingDtoValid
@Data
public class BookingDto {
    Long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @FutureOrPresent
    LocalDateTime end;
}
