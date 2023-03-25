package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIT {
    @Autowired
    private ItemServiceImpl itemService;

    @Test
    @Sql("classpath:add_users.sql")
    @Sql("classpath:add_items.sql")
    @Sql("classpath:add_bookings.sql")
    void getByUserId_whenUserHasNotItems_thenReturnEmptyList() {
        Long userId = 3L;
        Integer from = 0;
        Integer size = 4;

        Page<ItemDto> itemDtos = itemService.getByUserId(userId, from, size);

        assertThat(itemDtos.getContent().size(), equalTo(0));
    }

    @Test
    @Sql("classpath:add_users.sql")
    @Sql("classpath:add_items.sql")
    @Sql("classpath:add_bookings.sql")
    @Sql("classpath:add_comments.sql")
    void getByUserId() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 4;

        Page<ItemDto> itemDtos = itemService.getByUserId(userId, from, size);

        assertThat(itemDtos.getContent().size(), equalTo(2));
        assertThat(itemDtos.getContent().get(0).getId(), equalTo(1L));
        assertThat(itemDtos.getContent().get(0).getLastBooking().getId(), equalTo(1L));
        assertThat(itemDtos.getContent().get(0).getNextBooking().getId(), equalTo(2L));
        assertThat(itemDtos.getContent().get(0).getComments().size(), equalTo(1));
        assertThat(itemDtos.getContent().get(1).getId(), equalTo(2L));
        assertThat(itemDtos.getContent().get(1).getLastBooking().getId(), equalTo(4L));
        assertThat(itemDtos.getContent().get(1).getNextBooking(), equalTo(null));
        assertThat(itemDtos.getContent().get(1).getComments().size(), equalTo(0));
    }
}
