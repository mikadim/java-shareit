package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto dto);

    ItemDto update(Long userId, Long itemId, ItemDto dto);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> getByText(String text, Integer from, Integer size);

    CommentDto createComment(Long authorId, Long itemId, CommentDto dto);
}
