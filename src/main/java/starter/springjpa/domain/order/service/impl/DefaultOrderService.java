package starter.springjpa.domain.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.springjpa.domain.order.dto.OrderCreateDto;
import starter.springjpa.domain.order.dto.OrderReadDto;
import starter.springjpa.domain.order.entity.Item;
import starter.springjpa.domain.order.entity.Order;
import starter.springjpa.domain.order.repository.ItemRepository;
import starter.springjpa.domain.order.repository.OrderRepository;
import starter.springjpa.domain.order.service.OrderService;
import starter.springjpa.domain.user.entity.User;
import starter.springjpa.domain.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultOrderService implements OrderService {
    private final UserRepository  userRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository  itemRepository;

    @Override
    public UUID createOrder(UUID userId, OrderCreateDto createDto) {
        User user = userRepository.findByUuid(userId)
                .orElseThrow(RuntimeException::new);

        List<Item> items = itemRepository.findByUuidIn(createDto.getItemIds());
        Order order = orderRepository.save(new Order(user, items));

        order.addItem(items);

        return order.getUuid();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderReadDto getSingleOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findWithItemsByUuid(orderId)
                .orElseThrow(RuntimeException::new);
        return new OrderReadDto(order);
    }
}
