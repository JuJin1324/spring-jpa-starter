package practice.jpastarter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import practice.jpastarter.models.Member;
import practice.jpastarter.models.Schedule;

import java.util.Optional;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s " +
            "left join fetch s.scheduleMembers m " +
            "left join fetch m.member " +
            "where s.id = :id")
    Optional<Schedule> findWithAllById(Long id);
}
