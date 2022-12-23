package practice.jpastarter.domain.order.service;

import practice.jpastarter.domain.order.dto.OrderCreateDto;
import practice.jpastarter.domain.order.dto.OrderReadDto;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface OrderService {
    UUID createOrder(UUID userId, OrderCreateDto createDto);

    OrderReadDto getSingleOrder(UUID orderId, UUID userId);
}
