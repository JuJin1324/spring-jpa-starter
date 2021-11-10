package practice.jpastarter.services.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.exceptions.ResourceDuplicateException;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/10
 */

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SdMemberServiceTest {
    public static final String NEW_MEMBER_NAME = "신규 유저1";
    public static final int    NEW_MEMBER_AGE  = 20;
    @Autowired
    SdMemberService    memberService;
    @Autowired
    SdMemberRepository memberRepository;

    @Test
    @DisplayName("[회원 생성] 신규 회원")
    void createMember_whenNewMember() {
        /* given */
        final String newMemberPhone = "01022220003";
        /* when */
        Long memberId = memberService.createMember(NEW_MEMBER_NAME, NEW_MEMBER_AGE, newMemberPhone);
        /* then */
        assertThat(memberId).isGreaterThan(0L);
    }

    @Test
    @DisplayName("[회원 생성] 중복 회원")
    void createMember_whenDuplicatedMember() {
        /* given */
        final String duplicatedMemberPhone = "01022220001";

        /* when, then */
        assertThrows(ResourceDuplicateException.class, () ->
                memberService.createMember(NEW_MEMBER_NAME, NEW_MEMBER_AGE, duplicatedMemberPhone));
    }

    @Test
    @DisplayName("[회원 생성] Soft Delete 된 회원 되살리기")
    void createMember_whenReviveDeleted() {
        /* given */
        final String deletedMemberPhone = "01022220002";
        SdMember deletedMember = memberRepository.findOneByPhoneWithDeleted(deletedMemberPhone)
                .orElse(null);
        assertThat(deletedMember.isDeleted()).isEqualTo(true);

        /* when */
        Long memberId = memberService.createMember(NEW_MEMBER_NAME, NEW_MEMBER_AGE, deletedMemberPhone);
        assertThat(memberId).isEqualTo(deletedMember.getId());
        assertThat(deletedMember.isDeleted()).isEqualTo(false);
    }
}
