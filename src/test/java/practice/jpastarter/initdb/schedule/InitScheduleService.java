package practice.jpastarter.initdb.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
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
 * Created Date : 2021/11/08
 */

@Profile("test")
@Service
@RequiredArgsConstructor
public class InitScheduleService {
    private final HdMemberRepository hdMemberRepository;
    private final SdMemberRepository sdMemberRepository;

    @PostConstruct
    public void postConstruct() {
        List<SdMember> sdMembers = Arrays.asList(
                new SdMember(UUID.randomUUID().toString(), "기존 유저1", 10, "01011110001"),
                new SdMember(UUID.randomUUID().toString(), "기존 유저2", 20, "01011110002"),
                new SdMember(UUID.randomUUID().toString(), "신규 유저3", 30, "01011110003"));
        sdMemberRepository.saveAll(sdMembers);

        List<HdMember> hdMembers = Arrays.asList(
                new HdMember(UUID.randomUUID().toString(), "기존 유저1", 10, "01011110001"),
                new HdMember(UUID.randomUUID().toString(), "기존 유저2", 20, "01011110002"),
                new HdMember(UUID.randomUUID().toString(), "신규 유저3", 30, "01011110003"));
        hdMemberRepository.saveAll(hdMembers);
    }
}
