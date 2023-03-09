package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toItem(ItemDto dto);

    List<ItemDto> toDtoItems(List<Item> item);

    @Mapping(target = "description", qualifiedBy = BlankStringToNull.class)
    @Mapping(target = "name", qualifiedBy = BlankStringToNull.class)
    void updateItem(Item item, @MappingTarget Item itemForUpdate);

    @BlankStringToNull
    default String blankStringToNull(String s) {
        return s.isBlank() ? null : s;
    }

    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface BlankStringToNull {
    }
}

