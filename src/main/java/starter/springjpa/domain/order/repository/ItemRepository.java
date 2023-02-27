package starter.springjpa.domain.order.repository;

import starter.springjpa.domain.entity.repository.CommonRepository;
import starter.springjpa.domain.order.entity.Item;

import java.util.List;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/23
 */
//public interface ItemRepository extends JpaRepository<Item, Long> {
public interface ItemRepository extends CommonRepository<Item, Long> {


    <S extends Item> S save(S entity);

    List<Item> findByUuidIn(List<UUID> uuids);
}
