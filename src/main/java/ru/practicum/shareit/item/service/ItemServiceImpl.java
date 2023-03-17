package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.exception.CommentServiceException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemStatus;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public ItemServiceImpl(@Qualifier("itemDbStorage") ItemRepository itemRepository, ItemMapper itemMapper,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           CommentMapper commentMapper, @Qualifier("dbStorage") UserRepository userRepository,
                           BookingMapper bookingMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public ItemDto create(Long userId, ItemDto dto) {
        if (BooleanUtils.isNotTrue(dto.getAvailable()) || StringUtils.isBlank(dto.getName()) ||
                StringUtils.isBlank(dto.getDescription())) {
            throw new ItemServiceException("недопустимые свойства вещи");
        }
        Item item = itemMapper.toItem(dto);
        item.setOwner(userId);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        Item item = itemMapper.toItem(dto);
        item.setId(itemId);
        return itemMapper.toDto(itemRepository.update(userId, item));
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        ItemDto itemDto = itemMapper.toDto(itemRepository.getById(itemId));
        prepareDto(itemId, userId, itemDto);
        return itemDto;
    }

    private void prepareDto(Long itemId, Long userId, ItemDto itemDto) {
        List<Booking> itemBooking = bookingRepository.findByItemIdAndItemOwner(itemId, userId);
        itemBooking.stream()
                .filter(i -> i.getEnd().isBefore(LocalDateTime.now().plusSeconds(5)) && !i.getStatus().equals(ItemStatus.REJECTED))
                .max(Comparator.comparing(Booking::getEnd))
                .ifPresent(lastBooking -> itemDto.setLastBooking(bookingMapper.toBookingItemDto(lastBooking)));
        itemBooking.stream()
                .filter(i -> i.getStart().isAfter(LocalDateTime.now()) && !i.getStatus().equals(ItemStatus.REJECTED))
                .min(Comparator.comparing(Booking::getStart))
                .ifPresent(nextBooking -> itemDto.setNextBooking(bookingMapper.toBookingItemDto(nextBooking)));
        itemDto.setComments(commentMapper.toDto(commentRepository.findByItemId(itemId)));
    }

    @Override
    public List<ItemDto> getByUserId(Long userId, Integer from, Integer size) {

        Pageable page;
        if (size == null || from == null) {
            page = Pageable.unpaged();
        } else {
            Sort sortById = Sort.by(Sort.Direction.ASC, "id");
            page = PageRequest.of(from / size, size, sortById);
        }
        Page<Item> itemPage = itemRepository.getByUserId(userId, page);
        List<ItemDto> itemDtos = itemMapper.toDtoItems(itemPage.getContent());
        for (ItemDto dto : itemDtos) {
            prepareDto(dto.getId(), userId, dto);
        }
        return itemDtos.stream()
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByText(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Pageable page;
        if (size == null || from == null) {
            page = Pageable.unpaged();
        } else {
            Sort sortById = Sort.by(Sort.Direction.ASC, "id");
            page = PageRequest.of(from / size, size, sortById);
        }
        Page<Item> itemPage = itemRepository.getByText(text, page);
        return itemMapper.toDtoItems(itemPage.getContent());
    }

    @Override
    public CommentDto createComment(Long authorId, Long itemId, CommentDto dto) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, authorId).stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new CommentServiceException("отзыв на эту вещь добавить невозможно");
        }
        Comment comment = commentMapper.toComment(dto);
        comment.setItem(itemRepository.getById(itemId));
        comment.setAuthor(userRepository.getById(authorId));
        commentRepository.save(comment);
        log.info("Комментарий добавлен: {}", comment.toString());
        return commentMapper.toDto(comment);
    }
}
