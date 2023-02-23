package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long userId, ItemDto dto) {
        if (dto.getAvailable() == null || dto.getAvailable().equals(false) ||
                dto.getName() == null || dto.getName().isBlank() ||
                dto.getDescription() == null || dto.getDescription().isBlank()) {
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
        return itemMapper.toDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        return itemMapper.toDtoItems(itemRepository.getByUserId(userId));
    }

    @Override
    public List<ItemDto> getByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.toDtoItems(itemRepository.getByText(text));
    }
}
