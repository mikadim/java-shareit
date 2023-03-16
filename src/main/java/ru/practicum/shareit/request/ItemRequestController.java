package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String REQUESTOR_ID_TAG = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(REQUESTOR_ID_TAG) @NotNull @Positive Long requestorId,
                                                        @RequestBody @NotNull @Valid ItemRequestDto dto) {
        return new ResponseEntity<>(itemRequestService.create(dto, requestorId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(@RequestHeader(REQUESTOR_ID_TAG) @NotNull @Positive Long requestorId) {
        return new ResponseEntity<List<ItemRequestDto>>(itemRequestService.getUserRequests(requestorId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getUserRequest(@RequestHeader(REQUESTOR_ID_TAG) @NotNull @Positive Long requestorId,
                                                         @PathVariable("requestId") @Positive Long itemRequestId) {
        return new ResponseEntity<ItemRequestDto>(itemRequestService.getRequest(requestorId, itemRequestId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequest(@RequestHeader(REQUESTOR_ID_TAG) @NotNull @Positive Long requestorId,
                                                              @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                              @RequestParam(name = "size", required = false) @Positive Integer size) {
        return new ResponseEntity<List<ItemRequestDto>>(itemRequestService.getAll(requestorId, from, size), HttpStatus.OK);
    }
}
