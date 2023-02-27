package starter.springjpa.domain.order.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.springjpa.domain.order.entity.Order;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderReadDto {
    private UUID                   id;
    private ZonedDateTime          orderDateUTC;
    private List<OrderItemReadDto> items;

    public OrderReadDto(Order order) {
        this.id = order.getUuid();
        this.orderDateUTC = order.getOrderDateUTC();
        this.items = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemReadDto(orderItem.getItem()))
                .collect(Collectors.toList());
    }
}
