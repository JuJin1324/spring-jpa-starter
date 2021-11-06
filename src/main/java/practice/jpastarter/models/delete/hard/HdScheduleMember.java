package practice.jpastarter.models.delete.hard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.jpastarter.models.BaseEntity;

import javax.persistence.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Entity
@Table(name = "HD_SCHEDULE_MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HdScheduleMember extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "SCHEDULE_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID")
    private HdSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private HdMember member;

    public HdScheduleMember(HdSchedule schedule, HdMember member) {
        this.schedule = schedule;
        this.member = member;
    }

    public Long getMemberId() {
        return getMember().getId();
    }
}
