package practice.jpastarter.models.delete.soft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Table(name = "SD_SCHEDULE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SdSchedule extends SoftDeleteEntity {
    @Id
    @GeneratedValue
    @Column(name = "SCHEDULE_ID")
    private Long id;

    private String title;

    private LocalDateTime startTimeUTC;

    private LocalDateTime endTimeUTC;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<SdScheduleMember> scheduleMembers = new ArrayList<>();

    protected SdSchedule(String title, LocalDateTime startTimeUTC, LocalDateTime endTimeUTC) {
        this.title = title;
        this.startTimeUTC = startTimeUTC;
        this.endTimeUTC = endTimeUTC;
    }

    public static SdSchedule newSchedule(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, SdMember... members) {
        LocalDateTime startTimeUTC = startTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime endTimeUTC = endTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        SdSchedule schedule = new SdSchedule(title, startTimeUTC, endTimeUTC);
        List<SdScheduleMember> scheduleMembers = Arrays.stream(members)
                .map(member -> new SdScheduleMember(schedule, member))
                .collect(Collectors.toList());
        schedule.scheduleMembers.addAll(scheduleMembers);
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

    public void update(String title, ZonedDateTime startTimeKST, ZonedDateTime endTimeKST, SdMember... members) {
        this.title = title;
        this.startTimeUTC = startTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        this.endTimeUTC = endTimeKST.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        /* Soft Delete 방식 */
        Arrays.stream(members).forEach(this::addMember);
    }

    public void addMember(SdMember member) {
        Optional<SdScheduleMember> optional = findScheduleMemberWithDeleted(member.getId());
        if (optional.isPresent()) {
            SdScheduleMember scheduleMember = optional.get();
            if (scheduleMember.isDeleted()) {
                scheduleMember.unDelete();
            }
        } else {
            this.scheduleMembers.add(new SdScheduleMember(this, member));
        }
    }

    /* delFlag 와 상관없는 SdScheduleMember */
    /* delFlag = 'N' 체크된 SdScheduleMember */
    public Optional<SdScheduleMember> findScheduleMember(Long memberId) {
        return this.findScheduleMemberWithDeleted(memberId)
                .filter(scheduleMember -> !scheduleMember.isDeleted())
                .findAny();
    }

    public List<SdScheduleMember> getScheduleMembers() {
        return this.scheduleMembers.stream()
                .filter(scheduleMember -> !scheduleMember.isDeleted())
                .collect(Collectors.toList());
    }

    private Optional<SdScheduleMember> findScheduleMemberWithDeleted(Long memberId) {
        return this.scheduleMembers.stream()
                .filter(scheduleMember -> scheduleMember.getMemberId().equals(memberId))
                .findAny();
    }
}
