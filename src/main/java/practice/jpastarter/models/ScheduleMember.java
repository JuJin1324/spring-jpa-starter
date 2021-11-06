package practice.jpastarter.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Entity
@Table(name = "SCHEDULE_MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScheduleMember extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "SCHEDULE_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULE_ID")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public ScheduleMember(Schedule schedule, Member member) {
        this.schedule = schedule;
        this.member = member;
    }
}
