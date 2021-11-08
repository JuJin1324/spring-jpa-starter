package practice.jpastarter.services.schedule;

import practice.jpastarter.dtos.ScheduleDto;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface ScheduleService {
    Long createSchedule(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds);

    void updateSchedule(Long scheduleId, String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds);

    ScheduleDto getSingleSchedule(Long scheduleId);

    void deleteMembersInSchedule(Long scheduleId, List<Long> memberIds);
}
