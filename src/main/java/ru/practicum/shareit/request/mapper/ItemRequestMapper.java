package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    ItemRequest toItemRequest(ItemRequestDto dto);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toItemsRequestsDto(List<ItemRequest> itemRequest);
}
