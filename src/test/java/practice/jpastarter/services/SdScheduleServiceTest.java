package practice.jpastarter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.jpastarter.dtos.MemberDto;
import practice.jpastarter.dtos.ScheduleDto;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;
import practice.jpastarter.services.schedule.SdScheduleService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@SpringBootTest
class SdScheduleServiceTest {
    private static final String         OLD_TITLE      = "Spring Test 기존 일정 타이틀";
    private static final String         NEW_TITLE      = "Spring Test 신규 일정 타이틀";
    private static final ZonedDateTime  START_TIME_KST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    private static final ZonedDateTime  END_TIME_KST   = START_TIME_KST.plusHours(2);
    private              List<SdMember> members;

    @Autowired
    private SdMemberRepository memberRepository;
    @Autowired
    private SdScheduleService  scheduleService;

    @BeforeEach
    void setUp() {
        members = Arrays.asList(
                new SdMember(UUID.randomUUID().toString(), "기존 유저1", 10, "01011110001"),
                new SdMember(UUID.randomUUID().toString(), "기존 유저2", 20, "01011110002"),
                new SdMember(UUID.randomUUID().toString(), "신규 유저3", 30, "01011110003"));
        memberRepository.saveAll(members);
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 이미 있는 유저를 추가")
    void updateSchedule_whenAddUserThatAlreadyInSchedule() {
        /* given */
        Long scheduleId = givenSchedule();
        SdMember oldMember = members.stream()
                .filter(member -> member.getAge() == 10)
                .findFirst()
                .orElse(null);

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 추가 */
        scheduleService.updateSchedule(scheduleId, NEW_TITLE, START_TIME_KST, END_TIME_KST, Collections.singletonList(oldMember.getId()));

        /* then: ["기존 유저1", "기존 유저2"] 유지 */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactly("기존 유저1", "기존 유저2");
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 없는 유저를 추가")
    void updateSchedule_whenAddUserThatNotInSchedule() {
        /* given */
        Long scheduleId = givenSchedule();
        SdMember newMember = members.stream()
                .filter(member -> member.getAge() == 30)
                .findFirst()
                .orElse(null);

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 추가 */
        scheduleService.updateSchedule(scheduleId, NEW_TITLE, START_TIME_KST, END_TIME_KST, Collections.singletonList(newMember.getId()));

        /* then: ["기존 유저1", "기존 유저2"] 유지 */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactly("기존 유저1", "기존 유저2", "신규 유저3");
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 삭제되었던 유저를 추가")
    void updateSchedule_whenAddUserThatDeletedInSchedule() {
        /* given */
        Long scheduleId = givenSchedule();
        SdMember newMember = members.stream()
                .filter(member -> member.getAge() == 10)
                .findFirst()
                .orElse(null);
        List<Long> memberIds = Collections.singletonList(newMember.getId());
        scheduleService.deleteMembersInSchedule(scheduleId, memberIds);

        /* when: ["기존 유저2"] 에 "기존 유저1" 추가 */
        scheduleService.updateSchedule(scheduleId, NEW_TITLE, START_TIME_KST, END_TIME_KST, memberIds);

        /* then: ["기존 유저1", "기존 유저2"] */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("기존 유저1", "기존 유저2");
    }

    @Test
    @DisplayName("[일정 회원 삭제] 일정에 있는 유저 1명 삭제")
    void deleteMembersInSchedule_whenDeleteOneMember() {
        /* given */
        Long scheduleId = givenSchedule();
        SdMember sdMember = members.stream()
                .filter(member -> member.getAge() == 10)
                .findFirst()
                .orElse(null);
        List<Long> memberIds = Collections.singletonList(sdMember.getId());

        /* when: ["기존 유저1", "기존 유저2"] 에 "기존 유저1" 삭제 */
        scheduleService.deleteMembersInSchedule(scheduleId, memberIds);

        /* then: ["기존 유저2"] */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        assertThat(singleSchedule.getMemberDtos().stream().map(MemberDto::getName).collect(Collectors.toList()))
                .containsExactly("기존 유저2");
    }

    private Long givenSchedule() {
        List<Long> memberIds = members.stream()
                .filter(member -> member.getAge() != 30)
                .map(SdMember::getId)
                .collect(Collectors.toList());
        return scheduleService.createSchedule(OLD_TITLE, START_TIME_KST, END_TIME_KST, memberIds);
    }
}
