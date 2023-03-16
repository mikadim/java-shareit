package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndItemOwner(Long bookingId, Long ownerId);

    @Query(" select b from Booking b " +
            "inner join b.item i " +
            "inner join b.booker u " +
            "where b.id = ?1 and (i.owner = ?2 or u.id = ?2)")
    Optional<Booking> findByIdAndBookerIdAndItemOwner(Long bookingId, Long userId);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemOwner(Long ownerId);

    List<Booking> findByItemIdAndItemOwner(Long itemId, Long userId);

    List<Booking> findByItemIdAndBookerId(Long itemId, Long bookerId);
}
