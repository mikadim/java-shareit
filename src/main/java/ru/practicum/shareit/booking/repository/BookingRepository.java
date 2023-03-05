package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.ItemStatus;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    public Optional<Booking> findByIdAndItemOwner(Long bookingId, Long ownerId);

    @Query(" select b from Booking b " +
            "inner join b.item i " +
            "inner join b.booker u " +
            "where b.id = ?1 and (i.owner = ?2 or u.id = ?2)")
    public Optional<Booking> findByIdAndBookerIdAndItemOwner(Long bookingId, Long userId);

    public List<Booking> findByBookerId(Long bookerId);

    public List<Booking> findByItemOwner(Long ownerId);

    public List<BookingForItemDto> findByItemId(Long itemId);
}
