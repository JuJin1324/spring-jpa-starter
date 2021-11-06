package practice.jpastarter.dtos;

import lombok.Getter;
import lombok.ToString;
import practice.jpastarter.models.Member;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Getter
@ToString
public class MemberDto {
    private final Long   memberId;
    private final String name;
    private final int    age;

    public MemberDto(Long memberId, String name, int age) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
    }

    public MemberDto(Member member) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.age = member.getAge();
    }
}
