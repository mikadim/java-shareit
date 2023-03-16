package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingDtoValidator.class)
public @interface BookingDtoValid {
    String message() default "проверьте даты бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
