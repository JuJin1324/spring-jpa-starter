package practice.jpastarter.models.delete.hard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.jpastarter.models.BaseEntity;
import practice.jpastarter.models.delete.soft.SdScheduleMember;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Entity
@Table(name = "HD_SCHEDULE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HdSchedule extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "SCHEDULE_ID")
    private Long id;

    private String title;

    private LocalDateTime startTimeUTC;

    private LocalDateTime endTimeUTC;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<HdScheduleMember> scheduleMembers = new ArrayList<>();

    protected HdSchedule(String title, LocalDateTime startTimeUTC, LocalDateTime endTimeUTC) {
        this.title = title;
        this.startTimeUTC = startTimeUTC;
        this.endTimeUTC = endTimeUTC;
    }

    public static HdSchedule newSchedule(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, HdMember... members) {
        LocalDateTime startTimeUTC = startTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTimeUTC = endTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        HdSchedule schedule = new HdSchedule(title, startTimeUTC, endTimeUTC);
        List<HdScheduleMember> scheduleMembers = Arrays.stream(members)
                .map(member -> new HdScheduleMember(schedule, member))
                .collect(Collectors.toList());
        schedule.getScheduleMembers().addAll(scheduleMembers);
        return schedule;
    }

    public ZonedDateTime getStartTimeKST() {
        if (startTimeUTC == null) {
            throw new NullPointerException("Schedule 객체의 startTimeUTC 이 null 입니다.");
        }
        return ZonedDateTime.of(startTimeUTC, ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    }

    public ZonedDateTime getEndTimeKST() {
        if (startTimeUTC == null) {
            throw new NullPointerException("Schedule 객체의 endTimeUTC 이 null 입니다.");
        }
        return ZonedDateTime.of(endTimeUTC, ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    }

    public void update(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, HdMember... members) {
        this.title = title;
        this.startTimeUTC = startTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        this.endTimeUTC = endTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        /* Hard Delete 방식 */
        List<HdMember> filteredMembers = filterNotInScheduleMembers(Arrays.asList(members));
        List<HdScheduleMember> newScheduleMembers = filteredMembers.stream()
                .map(member -> new HdScheduleMember(this, member))
                .collect(Collectors.toList());
        this.getScheduleMembers().addAll(newScheduleMembers);
    }

    public Optional<HdScheduleMember> findScheduleMember(Long memberId) {
        return this.getScheduleMembers().stream()
                .filter(scheduleMember -> scheduleMember.getMemberId().equals(memberId))
                .findAny();
    }

    private List<HdMember> filterNotInScheduleMembers(List<HdMember> members) {
        return members.stream()
                .filter(member -> this.getScheduleMembers().stream()
                        .noneMatch(scheduleMember -> scheduleMember.getMemberId().equals(member.getId())))
                .collect(Collectors.toList());
    }
}
