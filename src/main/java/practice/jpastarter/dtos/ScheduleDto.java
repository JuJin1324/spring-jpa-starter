package practice.jpastarter.dtos;

import lombok.Getter;
import lombok.ToString;
import practice.jpastarter.models.delete.hard.HdSchedule;
import practice.jpastarter.models.delete.soft.SdSchedule;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Getter
@ToString
public class ScheduleDto {
    private final Long   scheduleId;
    private final String title;

    private final ZonedDateTime startTimeKST;

    private final ZonedDateTime endTimeKST;

    private final List<MemberDto> memberDtos;

    public ScheduleDto(Long scheduleId, String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<MemberDto> memberDtos) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.startTimeKST = startTimeKST;
        this.endTimeKST = endTimeKST;
        this.memberDtos = memberDtos;
    }

    public ScheduleDto(HdSchedule schedule) {
        this.scheduleId = schedule.getId();
        this.title = schedule.getTitle();
        this.startTimeKST = schedule.getStartTimeKST();
        this.endTimeKST = schedule.getEndTimeKST();
        this.memberDtos = schedule.getScheduleMembers().stream()
                .map(scheduleMember -> new MemberDto(scheduleMember.getMember()))
                .collect(Collectors.toList());
    }

    public ScheduleDto(SdSchedule schedule) {
        this.scheduleId = schedule.getId();
        this.title = schedule.getTitle();
        this.startTimeKST = schedule.getStartTimeKST();
        this.endTimeKST = schedule.getEndTimeKST();
        this.memberDtos = schedule.getScheduleMembers().stream()
                .map(scheduleMember -> new MemberDto(scheduleMember.getMember()))
                .collect(Collectors.toList());
    }
}
