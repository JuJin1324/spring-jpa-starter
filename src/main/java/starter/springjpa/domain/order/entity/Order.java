package starter.springjpa.domain.order.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.springjpa.global.repository.BaseTimeEntity;
import starter.springjpa.domain.user.entity.User;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"uuid"}, callSuper = false)
@Getter
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", columnDefinition = "BINARY(16)", updatable = false)
    private UUID uuid;

    @Column(name = "order_date")
    private ZonedDateTime orderDateUTC;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<OrderItem> orderItems = new HashSet<>();

    public Order(User user, List<Item> items) {
        this.uuid = UUID.randomUUID();
        this.orderDateUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        this.user = user;
        this.addItem(items);
    }

    public void addItem(List<Item> items) {
        items.stream()
                .map(item -> new OrderItem(this, item))
                .forEach(this.orderItems::add);
    }
}
