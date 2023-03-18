package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIT {
    @Autowired
    private ItemRequestServiceImpl itemRequestService;


    @Test
    @Sql("classpath:add_users.sql")
    @Sql("classpath:add_itemRequests.sql")
    void getAll() {
        Long requestorId = 1L;
        Integer from = null;
        Integer size = null;

        List<ItemRequestDto> size4 = itemRequestService.getAll(requestorId, from, size);
        List<ItemRequestDto> size2 = itemRequestService.getAll(requestorId, 2, 2);
        List<ItemRequestDto> size1 = itemRequestService.getAll(requestorId, 0, 1);
        List<ItemRequestDto> size0 = itemRequestService.getAll(requestorId, 4, 4);

        assertThat(size4.size(), equalTo(4));
        assertThat(size2.size(), equalTo(2));
        assertThat(size1.size(), equalTo(1));
        assertThat(size0.size(), equalTo(0));
    }
}
