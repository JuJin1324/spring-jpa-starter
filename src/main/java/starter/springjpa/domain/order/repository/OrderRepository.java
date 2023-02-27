package starter.springjpa.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import starter.springjpa.domain.order.entity.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "inner join fetch o.orderItems ot " +
            "inner join fetch ot.item i " +
            "where o.uuid = :uuid")
    Optional<Order> findWithItemsByUuid(@Param("uuid") UUID uuid);

    <S extends Order> S save(S entity);
}
