package practice.jpastarter.dtos;

import lombok.*;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ScheduleDto {
    private Long   scheduleId;
    private String title;
    private ZonedDateTime startTimeKST;
    private ZonedDateTime endTimeKST;
    private List<Long> memberIds;
    private List<MemberDto> memberDtos;

    @Builder
    private ScheduleDto(Long scheduleId, String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds, List<MemberDto> memberDtos) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.startTimeKST = startTimeKST;
        this.endTimeKST = endTimeKST;
        this.memberIds = memberIds;
        this.memberDtos = memberDtos;
    }

    public static ScheduleDto toCreate(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds) {
        return ScheduleDto.builder()
                .title(title)
                .startTimeKST(startTimeKST)
                .endTimeKST(endTimeKST)
                .memberIds(memberIds)
                .build();
    }

    public static ScheduleDto toUpdate(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, List<Long> memberIds) {
        return ScheduleDto.builder()
                .title(title)
                .startTimeKST(startTimeKST)
                .endTimeKST(endTimeKST)
                .memberIds(memberIds)
                .build();
    }

    public static ScheduleDto toRead(HdSchedule schedule) {
        return ScheduleDto.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .startTimeKST(schedule.getStartTimeKST())
                .endTimeKST(schedule.getEndTimeKST())
                .memberDtos(schedule.getScheduleMembers().stream()
                        .map(scheduleMember -> MemberDto.toRead(scheduleMember.getMember()))
                        .collect(Collectors.toList()))
                .build();
    }

    public static ScheduleDto toRead(SdSchedule schedule) {
        return ScheduleDto.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .startTimeKST(schedule.getStartTimeKST())
                .endTimeKST(schedule.getEndTimeKST())
                .memberDtos(schedule.getScheduleMembers().stream()
                        .map(scheduleMember -> MemberDto.toRead(scheduleMember.getMember()))
                        .collect(Collectors.toList()))
                .build();
    }
}
