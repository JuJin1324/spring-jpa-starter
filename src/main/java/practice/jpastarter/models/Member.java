package practice.jpastarter.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Entity
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "AGE")
    private int age;

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Member member = (Member) o;
        return id != null && Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
