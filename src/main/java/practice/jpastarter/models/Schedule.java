package practice.jpastarter.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.jpastarter.dtos.MemberDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Entity
@Table(name = "SCHEDULE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "SCHEDULE_ID")
    private Long id;

    private String title;

    private LocalDateTime startTimeUTC;

    private LocalDateTime endTimeUTC;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<ScheduleMember> scheduleMembers = new ArrayList<>();

    protected Schedule(String title, LocalDateTime startTimeUTC, LocalDateTime endTimeUTC) {
        this.title = title;
        this.startTimeUTC = startTimeUTC;
        this.endTimeUTC = endTimeUTC;
    }

    public static Schedule newSchedule(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, Member... members) {
        LocalDateTime startTimeUTC = startTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTimeUTC = endTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        Schedule schedule = new Schedule(title, startTimeUTC, endTimeUTC);
        List<ScheduleMember> scheduleMembers = Arrays.stream(members)
                .map(member -> new ScheduleMember(schedule, member))
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

    public void update(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, Member... members) {
        this.title = title;
        this.startTimeUTC = startTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        this.endTimeUTC = endTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        List<ScheduleMember> scheduleMembers = Arrays.stream(members)
                .map(member -> new ScheduleMember(this, member))
                .collect(Collectors.toList());
        this.getScheduleMembers().addAll(scheduleMembers);
    }
}
