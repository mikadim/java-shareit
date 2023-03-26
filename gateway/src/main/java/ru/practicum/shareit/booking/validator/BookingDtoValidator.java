package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDtoValidator implements ConstraintValidator<BookingDtoValid, BookingDto> {
    @Override
    public boolean isValid(BookingDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null || dto.getStart().isAfter(dto.getEnd()) ||
                dto.getStart().isEqual(dto.getEnd())) {
            return false;
        }
        return true;
    }
}
