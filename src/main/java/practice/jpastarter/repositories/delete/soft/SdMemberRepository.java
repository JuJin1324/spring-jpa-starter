package practice.jpastarter.repositories.delete.soft;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.CommonRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface SdMemberRepository extends CommonRepository<SdMember, Long> {
    @Query("select m from SdMember m where m.id in (:ids) and m.delFlag = 'N'")
    List<SdMember> findAllById(@Param("ids") List<Long> ids);

    @Query("select m from SdMember m where m.delFlag = 'N'")
    List<SdMember> findAll();

    @Query("select m from SdMember m where m.id = :id and m.delFlag = 'N'")
    Optional<SdMember> findOneById(@Param("id") Long id);

    @Query("select m from SdMember m where m.phone = :phone")
    Optional<SdMember> findOneByPhoneWithDeleted(@Param("phone") String phone);

    @Query("select m from SdMember m where m.uuid = :uuid")
    Optional<SdMember> findOneByUuidWithDeleted(@Param("uuid") String uuid);

    <S extends SdMember> List<S> saveAll(Iterable<S> entities);

    <S extends SdMember> S save(S entity);
}
