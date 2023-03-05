package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(@Qualifier("itemDbStorage") ItemRepository itemRepository, ItemMapper itemMapper,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.bookingRepository = bookingRepository;
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
    public ItemDto getById(Long itemId) {
        ItemDto itemDto = itemMapper.toDto(itemRepository.getById(itemId));
        List<BookingForItemDto> itemBooking = bookingRepository.findByItemId(itemId);
        itemBooking.stream()
                .filter((BookingForItemDto i) -> i.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingForItemDto::getEnd))
                .ifPresent(itemDto::setLastBooking);
        itemBooking.stream()
                .filter(i -> i.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingForItemDto::getStart))
                .ifPresent(itemDto::setNextBooking);
        return itemDto;
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        List<ItemDto> itemDtos = itemMapper.toDtoItems(itemRepository.getByUserId(userId));


        return itemDtos;
    }

    @Override
    public List<ItemDto> getByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.toDtoItems(itemRepository.getByText(text));
    }
}
