package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto create(Long userId, ItemDto dto);

    ItemDto update(Long userId, Long itemId, ItemDto dto);

    ItemDto getById(Long itemId, Long userId);

    Page<ItemDto> getByUserId(Long userId, Integer from, Integer size);

    Page<ItemDto> getByText(String text, Integer from, Integer size);

    CommentDto createComment(Long authorId, Long itemId, CommentDto dto);
}
