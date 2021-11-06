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
@Table(name = "HD_MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HdMember extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "AGE")
    private int age;

    public HdMember(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
