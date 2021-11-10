package practice.jpastarter.initdb.member;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.hard.HdMemberRepository;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/10
 */

@Profile("test")
@Component
@RequiredArgsConstructor
public class InitMemberService {
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
        private final HdMemberRepository hdMemberRepository;
        private final SdMemberRepository sdMemberRepository;

        public void initSdMember() {
            SdMember duplicatedMember = new SdMember(UUID.randomUUID().toString(), "중복 유저", 10, "01022220001");
            SdMember deletedMember = new SdMember(UUID.randomUUID().toString(), "삭제 유저", 20, "01022220002");
            List<SdMember> sdMembers = Arrays.asList(duplicatedMember, deletedMember);
            sdMemberRepository.saveAll(sdMembers);
            deletedMember.delete();
        }

        public void initHdMember() {
            List<HdMember> hdMembers = Arrays.asList(
                    new HdMember(UUID.randomUUID().toString(), "중복 유저", 10, "01022220001"));
            hdMemberRepository.saveAll(hdMembers);
        }
    }
}
