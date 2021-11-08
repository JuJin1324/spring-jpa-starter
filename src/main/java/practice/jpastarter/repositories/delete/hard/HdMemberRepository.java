package practice.jpastarter.repositories.delete.hard;

import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.repositories.delete.CommonRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface HdMemberRepository extends CommonRepository<HdMember, Long> {
    List<HdMember> findAllById(Iterable<Long> longs);

    List<HdMember> findAll();

    Optional<HdMember> findOneById(Long id);

    <S extends HdMember> List<S> saveAll(Iterable<S> entities);

    <S extends HdMember> S save(S entity);

    void deleteById(Long aLong);
}
