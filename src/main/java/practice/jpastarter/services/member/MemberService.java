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
    Long createMember(MemberDto memberDto);

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
    void updateMember(Long memberId, MemberDto memberDto);

    /**
     * 회원 삭제
     */
    void deleteMember(Long memberId);
}
