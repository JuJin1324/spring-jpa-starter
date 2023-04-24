package starter.springjpa.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import starter.springjpa.domain.user.entity.AbstractCustomData;
import starter.springjpa.domain.user.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface CustomDataRepository extends JpaRepository<AbstractCustomData, Long> {
}
