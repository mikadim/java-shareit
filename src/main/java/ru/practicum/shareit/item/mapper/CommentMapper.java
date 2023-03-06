package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment toComment(CommentDto dto);

    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);

    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "authorName", source = "comment.author.name")
    List<CommentDto> toDto(List<Comment> comment);
}
