package practice.jpastarter.repositories.delete.hard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.jpastarter.models.delete.hard.HdSchedule;
import practice.jpastarter.repositories.delete.CommonRepository;

import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface HdScheduleRepository extends CommonRepository<HdSchedule, Long> {

    @Query("select s from HdSchedule s " +
            "left join fetch s.scheduleMembers m " +
            "left join fetch m.member " +
            "where s.id = :id")
    Optional<HdSchedule> findWithAllById(@Param("id") Long id);

    <S extends HdSchedule> S save(S entity);
}
