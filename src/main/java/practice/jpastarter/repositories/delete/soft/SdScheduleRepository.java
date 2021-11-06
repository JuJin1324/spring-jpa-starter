package practice.jpastarter.repositories.delete.soft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.jpastarter.models.delete.soft.SdSchedule;

import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface SdScheduleRepository extends JpaRepository<SdSchedule, Long> {

    @Query("select s from SdSchedule s " +
            "left join fetch s.scheduleMembers sm " +
            "left join fetch sm.member " +
            "where s.id = :id " +
            "and s.delFlag = 'N'")
    Optional<SdSchedule> findWithAllById(@Param("id") Long id);
}
