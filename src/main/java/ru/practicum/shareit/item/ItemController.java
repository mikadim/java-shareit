package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    public static final String USER_ID_TAG = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(USER_ID_TAG) @NotNull Long userId,
                                              @RequestBody @NotNull ItemDto dto) {
        return new ResponseEntity<>(itemService.create(userId, dto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_ID_TAG) @NotNull Long userId,
                                              @PathVariable("id") Long itemId,
                                              @RequestBody @NotNull ItemDto dto) {
        return new ResponseEntity<>(itemService.update(userId, itemId, dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(USER_ID_TAG) Long userId,
                                           @PathVariable("id") Long itemId) {
        return new ResponseEntity<>(itemService.getById(itemId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader(USER_ID_TAG) @NotNull Long userId,
                                                  @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                  @RequestParam(name = "size", required = false) @Positive Integer size) {
        return new ResponseEntity<>(itemService.getByUserId(userId, from, size).getContent(), HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text,
                                                     @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                     @RequestParam(name = "size", required = false) @Positive Integer size) {
        return new ResponseEntity<>(itemService.getByText(text, from, size).getContent(), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader(USER_ID_TAG) @NotNull Long authorId,
                                                    @PathVariable("itemId") Long itemId,
                                                    @RequestBody @NotNull @Valid CommentDto dto) {
        return new ResponseEntity<>(itemService.createComment(authorId, itemId, dto), HttpStatus.OK);
    }
}
