package practice.jpastarter.services.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.MemberDto;
import practice.jpastarter.exceptions.GenerateUUIDException;
import practice.jpastarter.exceptions.ResourceDuplicateException;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/08
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SdMemberService implements MemberService {
    private final SdMemberRepository memberRepository;

    @Override
    public Long createMember(MemberDto memberDto) {
        return memberRepository.findOneByPhoneWithDeleted(memberDto.getPhone())
                .map(member -> {
                    // 중복 검사
                    if (!member.isDeleted()) {
                        throw new ResourceDuplicateException();
                    }
                    member.unDelete();
                    member.update(memberDto.getName(), memberDto.getAge(), memberDto.getPhone());
                    return member.getId();
                })
                .orElseGet(() -> {
                    SdMember newMember = memberRepository.save(
                            new SdMember(generateUUID(), memberDto.getName(), memberDto.getAge(), memberDto.getPhone())
                    );
                    return newMember.getId();
                });
    }

    @Override
    public MemberDto getSingleMember(Long memberId) {
        return memberRepository.findOneById(memberId)
                .map(MemberDto::toRead)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberDto::toRead)
                .collect(Collectors.toList());
    }

    @Override
    public void updateMember(Long memberId, MemberDto memberDto) {
        SdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        member.update(memberDto.getName(), memberDto.getAge(), memberDto.getPhone());
    }

    @Override
    public void deleteMember(Long memberId) {
        SdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        member.delete();
    }

    private String generateUUID() {
        final int retryCount = 10;
        for (int i = 0; i < retryCount; i++) {
            String uuid = UUID.randomUUID().toString();
            if (memberRepository.findOneByUuidWithDeleted(uuid).isEmpty()) {
                return uuid;
            }
        }
        throw new GenerateUUIDException();
    }
}
