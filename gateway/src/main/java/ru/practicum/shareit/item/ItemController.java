package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_TAG = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_TAG) @NotNull Long userId,
                                             @RequestBody @NotNull ItemDto dto) {
        log.info("Создать вещь {}", dto);
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_TAG) @NotNull Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody @NotNull ItemDto dto) {
        log.info("Обновить вещь id={}", itemId);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_TAG) Long userId,
                                          @PathVariable("id") Long itemId) {
        log.info("Получить вещь id={}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID_TAG) @NotNull Long userId,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получить все вещи пользователя id={}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Поиск вещей по ключевым словам - {}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_TAG) @NotNull Long authorId,
                                                @PathVariable("itemId") Long itemId,
                                                @RequestBody @NotNull @Valid CommentDto dto) {
        log.info("Создать для вещи id={} комментарий {}", itemId, dto);
        return itemClient.createComment(authorId, itemId, dto);
    }
}
