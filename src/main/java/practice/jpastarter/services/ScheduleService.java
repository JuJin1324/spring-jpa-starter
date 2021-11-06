package practice.jpastarter.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.ScheduleDto;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.Member;
import practice.jpastarter.models.Schedule;
import practice.jpastarter.repositories.MemberRepository;
import practice.jpastarter.repositories.ScheduleRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository   memberRepository;

    /**
     * 일정 생성
     *
     * @return Schedule ID
     */
    @Transactional
    public Long createSchedule(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Schedule schedule = Schedule.newSchedule(title, startTimeKST, endTimeKST, members.toArray(new Member[0]));
        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    /**
     * 일정 갱신
     */
    @Transactional
    public void updateSchedule(Long scheduleId, String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds) {
        List<Member> members = memberRepository.findAllById(memberIds);
        Schedule schedule = scheduleRepository.findWithAllById(scheduleId)
                .orElseThrow(ResourceNotFoundException::new);
        schedule.update(title, startTimeKST, endTimeKST, members.toArray(new Member[0]));
    }

    public ScheduleDto getSingleSchedule(Long scheduleId) {
        return scheduleRepository.findWithAllById(scheduleId)
                .map(ScheduleDto::new)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
