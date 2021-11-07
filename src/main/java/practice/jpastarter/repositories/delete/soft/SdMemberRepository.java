package practice.jpastarter.repositories.delete.soft;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.CommonRepository;

import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface SdMemberRepository extends CommonRepository<SdMember, Long> {
    @Query("select m from SdMember m where m.id in (:ids) and m.delFlag = 'N'")
    List<SdMember> findAllByIds(@Param("ids") List<Long> ids);

    <S extends SdMember> List<S> saveAll(Iterable<S> entities);
}
