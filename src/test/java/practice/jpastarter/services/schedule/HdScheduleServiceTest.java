package practice.jpastarter.services.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.MemberDto;
import practice.jpastarter.dtos.ScheduleDto;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.models.delete.hard.HdSchedule;
import practice.jpastarter.repositories.delete.hard.HdMemberRepository;
import practice.jpastarter.repositories.delete.hard.HdScheduleRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class HdScheduleServiceTest {
    private static final String        OLD_TITLE          = "Spring Test 기존 일정 타이틀";
    private static final String        NEW_TITLE          = "Spring Test 신규 일정 타이틀";
    private static final ZonedDateTime START_TIME_KST     = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    private static final ZonedDateTime END_TIME_KST       = START_TIME_KST.plusHours(2);
    private static final String        OLD_MEMBER_1_PHONE = "01011110001";
    private static final String        OLD_MEMBER_2_PHONE = "01011110002";
    private static final String        NEW_MEMBER_3_PHONE = "01011110003";

    @Autowired
    private HdMemberRepository   memberRepository;
    @Autowired
    private HdScheduleRepository scheduleRepository;
    @Autowired
    private HdScheduleService    scheduleService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 이미 있는 유저를 추가")
    void updateSchedule_whenAddUserThatAlreadyInSchedule() {
        /* given */
        Long scheduleId = givenOldScheduleId();
        HdMember oldMember = memberRepository.findOneByPhone(OLD_MEMBER_1_PHONE)
                .orElseThrow(ResourceNotFoundException::new);
        ScheduleDto givenScheduleDto = ScheduleDto.toUpdate(OLD_TITLE, START_TIME_KST, END_TIME_KST,
                Collections.singletonList(oldMember.getId()));

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 추가 */
        scheduleService.updateSchedule(scheduleId, givenScheduleDto);

        /* then: ["기존 유저1", "기존 유저2"] 유지 */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactly("기존 유저1", "기존 유저2");
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 없는 유저를 추가")
    void updateSchedule_whenAddUserThatNotInSchedule() {
        /* given */
        Long scheduleId = givenOldScheduleId();
        HdMember newMember = memberRepository.findOneByPhone(NEW_MEMBER_3_PHONE)
                .orElseThrow(ResourceNotFoundException::new);
        ScheduleDto givenScheduleDto = ScheduleDto.toUpdate(NEW_TITLE, START_TIME_KST, END_TIME_KST,
                Collections.singletonList(newMember.getId()));

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 추가 */
        scheduleService.updateSchedule(scheduleId, givenScheduleDto);

        /* then: ["기존 유저1", "기존 유저2"] 유지 */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactly("기존 유저1", "기존 유저2", "신규 유저3");
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 삭제되었던 유저를 추가")
    void updateSchedule_whenAddUserThatDeletedInSchedule() {
        /* given */
        Long scheduleId = givenOldScheduleId();
        HdMember newMember = memberRepository.findOneByPhone(OLD_MEMBER_1_PHONE)
                .orElseThrow(ResourceNotFoundException::new);
        List<Long> memberIds = Collections.singletonList(newMember.getId());
        scheduleService.deleteMembersInSchedule(scheduleId, memberIds);
        ScheduleDto givenScheduleDto = ScheduleDto.toUpdate(NEW_TITLE, START_TIME_KST, END_TIME_KST, memberIds);

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 삭제 */
        scheduleService.updateSchedule(scheduleId, givenScheduleDto);

        /* then: ["기존 유저1", "기존 유저2"] */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("기존 유저1", "기존 유저2");
    }

    @Test
    @DisplayName("[일정 회원 삭제] 일정에 있는 유저 1명 삭제")
    void deleteMembersInSchedule_whenDeleteOneMember() {
        /* given */
        Long scheduleId = givenOldScheduleId();
        HdMember hdMember = memberRepository.findOneByPhone(OLD_MEMBER_1_PHONE)
                .orElseThrow(ResourceNotFoundException::new);
        List<Long> memberIds = Collections.singletonList(hdMember.getId());

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 삭제 */
        scheduleService.deleteMembersInSchedule(scheduleId, memberIds);

        /* then: ["기존 유저2"] */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactly("기존 유저2");
    }

    private Long givenOldScheduleId() {
        return scheduleRepository.findByTitle(OLD_TITLE)
                .map(HdSchedule::getId)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
