package practice.jpastarter.services.schedule;

import practice.jpastarter.dtos.ScheduleDto;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */
public interface ScheduleService {
    Long createSchedule(ScheduleDto scheduleDto);

    void updateSchedule(Long scheduleId, ScheduleDto scheduleDto);

    ScheduleDto getSingleSchedule(Long scheduleId);

    void deleteMembersInSchedule(Long scheduleId, List<Long> memberIds);
}
