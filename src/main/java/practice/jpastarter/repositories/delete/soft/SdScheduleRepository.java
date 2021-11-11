package practice.jpastarter.repositories.delete.soft;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.jpastarter.models.delete.soft.SdSchedule;
import practice.jpastarter.repositories.delete.CommonRepository;

import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface SdScheduleRepository extends CommonRepository<SdSchedule, Long> {

    @Query("select s from SdSchedule s " +
            "left join fetch s.scheduleMembers sm " +
            "left join fetch sm.member " +
            "where s.id = :id " +
            "and s.delFlag = 'N'")
    Optional<SdSchedule> findWithAllById(@Param("id") Long id);

    <S extends SdSchedule> S save(S entity);

    /* for test purpose only below */
    @Query("select s from SdSchedule s " +
            "where s.title = :title " +
            "and s.delFlag = 'N'")
    Optional<SdSchedule> findByTitle(@Param("title") String title);
}
