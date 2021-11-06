package practice.jpastarter.models.delete.soft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Entity
@Table(name = "SD_SCHEDULE_MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SdScheduleMember extends SoftDeleteEntity {
    @Id
    @GeneratedValue
    @Column(name = "SCHEDULE_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID")
    private SdSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private SdMember member;

    public SdScheduleMember(SdSchedule schedule, SdMember member) {
        this.schedule = schedule;
        this.member = member;
    }

    public Long getMemberId() {
        return getMember().getId();
    }
}
