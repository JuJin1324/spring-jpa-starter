package practice.jpastarter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.jpastarter.models.Member;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
