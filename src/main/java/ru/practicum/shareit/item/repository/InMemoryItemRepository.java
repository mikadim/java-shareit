package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.IdGenerator;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

    @Override
    public Item getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .orElseGet(() -> {
                    throw new ItemRepositoryException(itemId + ": этот id не найден");
                });
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwner(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByText(String text) {
        final String prepText = text.trim().toLowerCase();
        return items.values().stream()
                .filter(i -> i.getAvailable().equals(true) &&
                        (i.getName().trim().toLowerCase().contains(prepText) || i.getDescription().trim().toLowerCase().contains(prepText)))
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        User owner = userRepository.getById(item.getOwner());
        item.setId(idGenerator.getId());
        items.put(item.getId(), item);
        log.info("Добавлена новая вещь: {}", item.toString());
        return items.get(item.getId());
    }

    @Override
    public Item update(Long userId, Item item) {
        Item updateItem = getById(item.getId());
        if (!updateItem.getOwner().equals(userId) || !updateItem.getId().equals(item.getId())) {
            throw new ItemRepositoryException("эта вещь недоступна для редактирования");
        }
        Optional.ofNullable(item.getName()).ifPresent(updateItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(updateItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(updateItem::setAvailable);
        log.info("Данные вещи обновлены: {}", updateItem.toString());
        return items.get(updateItem.getId());
    }
}
