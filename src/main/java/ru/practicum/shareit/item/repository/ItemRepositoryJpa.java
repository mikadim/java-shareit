package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {
    List<Item> findByRequestId(Long reuestId);

    Page<Item> findByOwner(Long owner, Pageable page);

    @Query(" select i from Item i " +
            "where i.available = true  and (lower(i.name) like concat('%', ?1, '%') or lower(i.description) like concat('%', ?1, '%'))")
    Page<Item> getByText(String text, Pageable page);
}
