package starter.springjpa.domain.order.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.springjpa.domain.order.entity.Item;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderItemReadDto {
    private UUID   id;
    private String name;

    public OrderItemReadDto(Item item) {
        this.id = item.getUuid();
        this.name = item.getName();
    }
}
