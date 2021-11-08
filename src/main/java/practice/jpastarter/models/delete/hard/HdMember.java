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

    @Column(name = "UUID", unique = true)
    private String uuid;

    @Column(name = "NAME")
    private String name;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "PHONE")
    private String phone;

    public HdMember(String uuid, String name, Integer age, String phone) {
        this.uuid = uuid;
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    public void update(String name, Integer age, String phone) {
        this.name = name;
        this.age = age;
        this.phone = phone;
    }
}
