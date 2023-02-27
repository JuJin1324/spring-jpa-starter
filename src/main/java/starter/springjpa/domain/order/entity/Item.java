package starter.springjpa.domain.order.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */

@Entity
@Table(name = "item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"uuid"}, callSuper = false)
@Getter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", columnDefinition = "BINARY(16)", updatable = false)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    public Item(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Item(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }
}
