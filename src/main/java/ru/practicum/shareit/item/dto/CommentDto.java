package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;
    @NotBlank
    String text;
    String authorName;
    LocalDateTime created;
}
