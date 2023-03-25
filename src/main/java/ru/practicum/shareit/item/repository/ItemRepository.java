package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item update(Long userId, Item item);

    Item getById(Long itemId);

    Page<Item> getByUserId(Long userId, Pageable page);

    Page<Item> getByText(String text, Pageable page);

    List<Item> getByRequest(Long requestId);
}