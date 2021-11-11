package practice.jpastarter.dtos;

import lombok.*;
import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.models.delete.soft.SdMember;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDto {
    private Long   memberId;
    private String name;
    private String phone;
    private int    age;

    @Builder
    private MemberDto(Long memberId, String name, String phone, int age) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.age = age;
    }

    public static MemberDto toCreate(String name, int age, String phone) {
        return MemberDto.builder()
                .name(name)
                .age(age)
                .phone(phone)
                .build();
    }

    public static MemberDto toRead(HdMember member) {
        return MemberDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .build();
    }

    public static MemberDto toRead(SdMember member) {
        return MemberDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .build();
    }

    public static MemberDto toUpdate(String name, int age, String phone) {
        return MemberDto.builder()
                .name(name)
                .age(age)
                .phone(phone)
                .build();
    }
}
