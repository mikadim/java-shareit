package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;


import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                              @RequestBody @NotNull ItemDto dto) {
        return new ResponseEntity<>(itemService.create(userId, dto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                              @PathVariable("id") Long itemId,
                                              @RequestBody @NotNull ItemDto dto) {
        return new ResponseEntity<>(itemService.update(userId, itemId, dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable("id") Long itemId) {
        return new ResponseEntity<>(itemService.getById(itemId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return new ResponseEntity<>(itemService.getByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        return new ResponseEntity<>(itemService.getByText(text), HttpStatus.OK);
    }
}
