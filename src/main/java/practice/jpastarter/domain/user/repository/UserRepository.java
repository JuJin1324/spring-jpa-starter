package practice.jpastarter.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.jpastarter.domain.user.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
//public interface UserRepository extends CommonRepository<User, Long> {
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(UUID uuid);
}
