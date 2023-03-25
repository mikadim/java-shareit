package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto dto, Long requestorId);

    List<ItemRequestDto> getUserRequests(Long requestorId);

    ItemRequestDto getRequest(Long requestorId, Long itemRequestId);

    List<ItemRequestDto> getAll(Long requestorId, Integer from, Integer size);
}
