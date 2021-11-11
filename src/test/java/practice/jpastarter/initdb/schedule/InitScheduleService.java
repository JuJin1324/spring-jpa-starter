package practice.jpastarter.initdb.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.ScheduleDto;
import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.hard.HdMemberRepository;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;
import practice.jpastarter.services.schedule.HdScheduleService;
import practice.jpastarter.services.schedule.SdScheduleService;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/08
 */

@Profile("test")
@Component
@RequiredArgsConstructor
public class InitScheduleService {
    private final InitService initService;

    @PostConstruct
    public void postConstruct() {
        initService.initHdMember();
        initService.initSdMember();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private static final String        OLD_TITLE          = "Spring Test 기존 일정 타이틀";
        private static final ZonedDateTime START_TIME_KST     = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        private static final ZonedDateTime END_TIME_KST       = START_TIME_KST.plusHours(2);
        private static final String        OLD_MEMBER_1_PHONE = "01011110001";
        private static final String        OLD_MEMBER_2_PHONE = "01011110002";
        private static final String        NEW_MEMBER_3_PHONE = "01011110003";

        private final HdMemberRepository hdMemberRepository;
        private final SdMemberRepository sdMemberRepository;
        private final HdScheduleService  hdScheduleService;
        private final SdScheduleService  sdScheduleService;

        public void initSdMember() {
            List<SdMember> sdMembers = Arrays.asList(
                    new SdMember(UUID.randomUUID().toString(), "기존 유저1", 10, OLD_MEMBER_1_PHONE),
                    new SdMember(UUID.randomUUID().toString(), "기존 유저2", 20, OLD_MEMBER_2_PHONE),
                    new SdMember(UUID.randomUUID().toString(), "신규 유저3", 30, NEW_MEMBER_3_PHONE));
            sdMemberRepository.saveAll(sdMembers);

            List<Long> memberIds = new ArrayList<>();
            memberIds.add(sdMembers.get(0).getId());
            memberIds.add(sdMembers.get(1).getId());

            ScheduleDto givenScheduleDto = ScheduleDto.toCreate(OLD_TITLE, START_TIME_KST, END_TIME_KST, memberIds);
            sdScheduleService.createSchedule(givenScheduleDto);
        }

        public void initHdMember() {
            List<HdMember> hdMembers = Arrays.asList(
                    new HdMember(UUID.randomUUID().toString(), "기존 유저1", 10, OLD_MEMBER_1_PHONE),
                    new HdMember(UUID.randomUUID().toString(), "기존 유저2", 20, OLD_MEMBER_2_PHONE),
                    new HdMember(UUID.randomUUID().toString(), "신규 유저3", 30, NEW_MEMBER_3_PHONE));
            hdMemberRepository.saveAll(hdMembers);

            List<Long> memberIds = new ArrayList<>();
            memberIds.add(hdMembers.get(0).getId());
            memberIds.add(hdMembers.get(1).getId());

            ScheduleDto givenScheduleDto = ScheduleDto.toCreate(OLD_TITLE, START_TIME_KST, END_TIME_KST, memberIds);
            hdScheduleService.createSchedule(givenScheduleDto);
        }
    }
}
