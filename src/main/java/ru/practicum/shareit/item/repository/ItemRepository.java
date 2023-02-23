package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item update(Long userId, Item item);

    Item getById(Long itemId);

    List<Item> getByUserId(Long userId);

    List<Item> getByText(String text);
}