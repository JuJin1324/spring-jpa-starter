package practice.jpastarter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.interceptor.NamedCacheResolver;
import practice.jpastarter.dtos.ScheduleDto;
import practice.jpastarter.models.Member;
import practice.jpastarter.models.Schedule;
import practice.jpastarter.repositories.MemberRepository;
import practice.jpastarter.repositories.ScheduleRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/06
 */

@SpringBootTest
class ScheduleServiceTest {
    private static final String        OLD_TITLE      = "Spring Test 기존 일정 타이틀";
    private static final String        NEW_TITLE      = "Spring Test 신규 일정 타이틀";
    private static final ZonedDateTime START_TIME_KST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    private static final ZonedDateTime END_TIME_KST   = START_TIME_KST.plusHours(2);
    private List<Member> members;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ScheduleService  scheduleService;

    @BeforeEach
    void setUp() {
        members = Arrays.asList(
                new Member("기존 유저1", 10),
                new Member("기존 유저2", 20),
                new Member("신규 유저3", 30));
        memberRepository.saveAll(members);
    }

    @Test
    @DisplayName("[일정 갱신] 일정에 이미 있는 유저를 추가")
    void updateSchedule_whenAddUserThatAlreadyInSchedule() {
        /* given */
        Long scheduleId = givenSchedule();
        Member oldMember = members.stream()
                .filter(member -> member.getAge() == 10)
                .findFirst()
                .orElse(null);
        /* when */
        scheduleService.updateSchedule(scheduleId, NEW_TITLE, START_TIME_KST, END_TIME_KST, Collections.singletonList(oldMember.getId()));

        /* then */
        ScheduleDto singleSchedule = scheduleService.getSingleSchedule(scheduleId);
        System.out.println("singleSchedule.getMemberDtos() = " + singleSchedule.getMemberDtos());
//        assertThat(singleSchedule.getTitle()).isEqualTo(NEW_TITLE);
//        assertThat(singleSchedule.getMemberDtos().size()).isEqualTo(2);
    }

    private Long givenSchedule() {
        List<Long> memberIds = members.stream()
                .filter(member -> member.getAge() != 30)
                .map(Member::getId)
                .collect(Collectors.toList());
        return scheduleService.createSchedule(OLD_TITLE, START_TIME_KST, END_TIME_KST, memberIds);
    }
}
