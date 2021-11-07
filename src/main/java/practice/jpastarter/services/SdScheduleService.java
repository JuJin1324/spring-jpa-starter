package practice.jpastarter.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.ScheduleDto;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.models.delete.soft.SdSchedule;
import practice.jpastarter.models.delete.soft.SdScheduleMember;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;
import practice.jpastarter.repositories.delete.soft.SdScheduleRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SdScheduleService implements ScheduleService {
    private final SdScheduleRepository scheduleRepository;
    private final SdMemberRepository   memberRepository;

    /**
     * 일정 생성
     *
     * @return Schedule ID
     */
    @Transactional
    @Override
    public Long createSchedule(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds) {
        List<SdMember> members = memberRepository.findAllByIds(memberIds);
        SdSchedule schedule = SdSchedule.newSchedule(title, startTimeKST, endTimeKST, members.toArray(new SdMember[0]));
        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    /**
     * 일정 갱신
     */
    @Transactional
    @Override
    public void updateSchedule(Long scheduleId, String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds) {
        List<SdMember> members = memberRepository.findAllByIds(memberIds);
        SdSchedule schedule = scheduleRepository.findWithAllById(scheduleId)
                .orElseThrow(ResourceNotFoundException::new);
        schedule.update(title, startTimeKST, endTimeKST, members.toArray(new SdMember[0]));
    }

    @Override
    public ScheduleDto getSingleSchedule(Long scheduleId) {
        return scheduleRepository.findWithAllById(scheduleId)
                .map(ScheduleDto::new)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    @Override
    public void deleteMembersInSchedule(Long scheduleId, List<Long> memberIds) {
        SdSchedule schedule = scheduleRepository.findWithAllById(scheduleId)
                .orElseThrow(ResourceNotFoundException::new);
        memberIds.forEach(memberId ->
                schedule.findScheduleMember(memberId).ifPresent(SdScheduleMember::delete));
    }
}
