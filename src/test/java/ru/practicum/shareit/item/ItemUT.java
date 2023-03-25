package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.model.ItemStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.item.model.ItemStatus.REJECTED;
import static ru.practicum.shareit.item.model.ItemStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class ItemUT {
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void prepareDto() {
        Long itemId = 1L;
        Long userId = 1L;
        Item item = new Item(1L, "testItem", "asdfsdaf", Boolean.TRUE, 1L, 1L);
        ItemDto itemDto = new ItemDto(itemId, "testName", "testDescription", Boolean.TRUE, null, null, null, null, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), item, null, APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), item, null, APPROVED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, null, REJECTED);
        Booking booking4 = new Booking(4L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item, null, APPROVED);
        Booking booking5 = new Booking(5L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(4), item, null, APPROVED);
        Booking booking6 = new Booking(6L, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), item, null, WAITING);
        List<Booking> bookings = List.of(booking1, booking2, booking3, booking4, booking5, booking6);
        Comment comment1 = new Comment(1L, "test1", item, null, LocalDateTime.now());
        Comment comment2 = new Comment(2L, "test2", item, null, LocalDateTime.now());
        List<Comment> comments = List.of(comment1, comment2);

        when(commentRepository.findByItemId(itemId)).thenReturn(comments);
        when(bookingRepository.findByItemIdAndItemOwner(itemId, userId)).thenReturn(bookings);

        ReflectionTestUtils.invokeMethod(itemService, "prepareDto", itemId, userId, itemDto);
        assertThat(itemDto.getLastBooking().getId(), equalTo(booking2.getId()));
        assertThat(itemDto.getNextBooking().getId(), equalTo(booking4.getId()));
        assertThat(itemDto.getComments().size(), equalTo(comments.size()));
    }
}
