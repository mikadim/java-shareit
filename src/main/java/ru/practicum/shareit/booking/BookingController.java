package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String BOOKER_ID_TAG = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Booking> crateBooking(@RequestHeader(BOOKER_ID_TAG) @NotNull Long bookerId,
                                                @RequestBody @NotNull @Valid BookingDto dto) {
        return new ResponseEntity<>(bookingService.create(dto, bookerId), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> changeBookingStatus(@RequestHeader(BOOKER_ID_TAG) @NotNull Long ownerId,
                                                       @PathVariable("bookingId") Long bookingId,
                                                       @RequestParam("approved") @NotNull Boolean status) {
        return new ResponseEntity<>(bookingService.updateStatus(ownerId, bookingId, status), HttpStatus.OK);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Booking> getBookingStatus(@RequestHeader(BOOKER_ID_TAG) @NotNull Long userId,
                                                    @PathVariable("bookingId") Long bookingId) {
        return new ResponseEntity<>(bookingService.getStatus(userId, bookingId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getBookerBookings(@RequestHeader(BOOKER_ID_TAG) @NotNull Long userId,
                                                         @RequestParam(value = "state", defaultValue = "ALL") BookingStatusDto status) {
        return new ResponseEntity<>(bookingService.getBookerBookings(userId, status), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Booking>> getUserBookings(@RequestHeader(BOOKER_ID_TAG) @NotNull Long userId,
                                                             @RequestParam(value = "state", defaultValue = "ALL") BookingStatusDto status) {
        return new ResponseEntity<>(bookingService.getUserBookings(userId, status), HttpStatus.OK);
    }
}
