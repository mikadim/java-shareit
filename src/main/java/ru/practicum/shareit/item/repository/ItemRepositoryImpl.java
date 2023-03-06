package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserRepositoryException;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Slf4j
@Repository("itemDbStorage")
public class ItemRepositoryImpl implements ItemRepository {
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private EntityManager entityManager;

    @Autowired
    public ItemRepositoryImpl(@Lazy ItemRepositoryJpa itemRepositoryJpa, @Qualifier("dbStorage") UserRepository userRepository,
                              ItemMapper itemMapper, EntityManager entityManager) {
        this.itemRepositoryJpa = itemRepositoryJpa;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.entityManager = entityManager;
    }

    @Override
    public Item save(Item item) {
        userRepository.getById(item.getOwner());
        Item newItem= itemRepositoryJpa.save(item);
        log.info("Добавлена новая вещь: {}", newItem.toString());
        return newItem;
    }

    @Override
    public Item update(Long userId, Item item) {
        Item itemForUpdate= getById(item.getId());
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
    public List<Item> getByUserId(Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Item> root = criteriaQuery.from(Item.class);
        criteriaQuery.select(root).where(cb.equal(root.get("owner"), userId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Item> getByText(String text) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate available = cb.isTrue(root.get("available"));
        Predicate likeText = cb.or((cb.like(cb.lower(root.get("name")), "%" + text.toLowerCase() + "%")),
                (cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        criteriaQuery.select(root).where(cb.and(available, likeText));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }


}
