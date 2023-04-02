package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String REQUESTOR_ID_TAG = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(REQUESTOR_ID_TAG) Long requestorId,
                                                        @RequestBody ItemRequestDto dto) {
        return new ResponseEntity<>(itemRequestService.create(dto, requestorId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(@RequestHeader(REQUESTOR_ID_TAG) Long requestorId) {
        return new ResponseEntity<List<ItemRequestDto>>(itemRequestService.getUserRequests(requestorId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getUserRequest(@RequestHeader(REQUESTOR_ID_TAG) Long requestorId,
                                                         @PathVariable("requestId") Long itemRequestId) {
        return new ResponseEntity<ItemRequestDto>(itemRequestService.getRequest(requestorId, itemRequestId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequest(@RequestHeader(REQUESTOR_ID_TAG) Long requestorId,
                                                              @RequestParam(name = "from", required = false) Integer from,
                                                              @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<List<ItemRequestDto>>(itemRequestService.getAll(requestorId, from, size), HttpStatus.OK);
    }
}
