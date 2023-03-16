package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.repository.UserRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.item.model.ItemStatus.WAITING;

@DataJpaTest
public class RepositoryIT {
    private User user;
    private Item item;
    private Booking booking;
    private User user2;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;


    @BeforeEach
    public void addData() {
        user = userRepositoryJpa.save(new User(null, "testMail@mail.ru", "testName"));
        item = itemRepositoryJpa.save(new Item(null, "testItem", "testDescription", Boolean.TRUE, user.getId(), null));
        booking = bookingRepository.save(new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user, WAITING));
        user2 = userRepositoryJpa.save(new User(null, "test2Mail@mail.ru", "test2Name"));
    }

    @Test
    public void findByIdAndBookerIdAndItemOwner() {
        Optional<Booking> presentBooking = bookingRepository.findByIdAndBookerIdAndItemOwner(booking.getId(), user.getId());
        Optional<Booking> notPresentBooking = bookingRepository.findByIdAndBookerIdAndItemOwner(booking.getId(), user2.getId());

        assertTrue(presentBooking.isPresent());
        assertFalse(notPresentBooking.isPresent());
    }

    @Test
    public void getByText() {
        Page<Item> presentItem = itemRepositoryJpa.getByText("test", Pageable.unpaged());
        Page<Item> notPresentItem = itemRepositoryJpa.getByText("simsalabim", Pageable.unpaged());

        assertTrue(presentItem.hasContent());
        assertFalse(notPresentItem.hasContent());
    }

}
