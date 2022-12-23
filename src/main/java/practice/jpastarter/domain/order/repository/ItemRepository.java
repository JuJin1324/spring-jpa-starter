package practice.jpastarter.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.jpastarter.domain.order.entity.Item;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUuidIn(List<UUID> uuids);
}
