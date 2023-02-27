package starter.springjpa.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import starter.springjpa.domain.order.dto.OrderCreateDto;
import starter.springjpa.domain.order.entity.Item;
import starter.springjpa.domain.order.entity.Order;
import starter.springjpa.domain.order.repository.ItemRepository;
import starter.springjpa.domain.order.repository.OrderRepository;
import starter.springjpa.domain.order.service.OrderService;
import starter.springjpa.domain.user.entity.User;
import starter.springjpa.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */

@SpringBootTest
@Transactional
public class OrderServiceIntegrationTest {
    static final UUID       USER_ID  = UUID.randomUUID();
    static final List<UUID> ITEM_IDS = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    @Autowired
    UserRepository  userRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository  itemRepository;

    @Autowired
    OrderService orderService;

    @Test
    void createOrder() {
        /* given */
        User user = givenUser();

        List<UUID> itemIds = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        itemIds.add(uuid);
        itemIds.add(uuid);
        List<Item> items = givenItems(itemIds);

        /* when */
        OrderCreateDto createDto = new OrderCreateDto(itemIds);
        UUID orderId = orderService.createOrder(user.getUuid(), createDto);

        /* then */
        Order order = orderRepository.findWithItemsByUuid(orderId).get();
        assertEquals(1, order.getOrderItems().size());
    }

    private User givenUser() {
        return userRepository.save(new User(USER_ID, "user name"));
    }

    private List<Item> givenItems(List<UUID> itemIds) {
        return itemIds.stream()
                .distinct()
                .map(itemId -> itemRepository.save(new Item(itemId, "item name " + itemId)))
                .collect(Collectors.toList());
    }
}
