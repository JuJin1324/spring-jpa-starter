package practice.jpastarter.services.member;

import practice.jpastarter.dtos.MemberDto;

import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/08
 */
public interface MemberService {
    /**
     * 회원 생성
     */
    Long createMember(String name, Integer age, String phone);

    /**
     * 회원 조회 - 단건
     */
    MemberDto getSingleMember(Long memberId);

    /**
     * 회원 조회 - 전체
     */
    List<MemberDto> getAllMembers();

    /**
     * 회원 갱신
     */
    void updateMember(Long memberId, String name, Integer age, String phone);

    /**
     * 회원 삭제
     */
    void deleteMember(Long memberId);
}
