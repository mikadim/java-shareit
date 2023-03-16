package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserRepositoryException;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository("itemDbStorage")
public class ItemRepositoryImpl implements ItemRepository {
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private EntityManager entityManager;

    @Autowired
    public ItemRepositoryImpl(ItemRepositoryJpa itemRepositoryJpa, @Qualifier("dbStorage") UserRepository userRepository,
                              ItemMapper itemMapper, EntityManager entityManager) {
        this.itemRepositoryJpa = itemRepositoryJpa;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.entityManager = entityManager;
    }

    @Override
    public Item save(Item item) {
        userRepository.getById(item.getOwner());
        Item newItem = itemRepositoryJpa.save(item);
        log.info("Добавлена новая вещь: {}", newItem.toString());
        return newItem;
    }

    @Override
    public Item update(Long userId, Item item) {
        Item itemForUpdate = getById(item.getId());
        if (!itemForUpdate.getOwner().equals(userId) || !itemForUpdate.getId().equals(item.getId())) {
            throw new ItemRepositoryException("эта вещь недоступна для редактирования");
        }
        itemMapper.updateItem(item, itemForUpdate);
        itemRepositoryJpa.save(itemForUpdate);
        log.info("Данные вещи обновлены: {}", itemForUpdate.toString());
        return itemForUpdate;
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepositoryJpa.findById(itemId)
                .orElseGet(() -> {
                    throw new UserRepositoryException(itemId + ": этот id не найден");
                });
    }

    @Override
    public Page<Item> getByUserId(Long userId, Pageable page) {
        return itemRepositoryJpa.findByOwner(userId, page);
    }

    @Override
    public Page<Item> getByText(String text, Pageable page) {
        return itemRepositoryJpa.getByText(text.toLowerCase(), page);
    }

    @Override
    public List<Item> getByRequest(Long requestId) {
        return itemRepositoryJpa.findByRequestId(requestId);
    }
}
