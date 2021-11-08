package practice.jpastarter.services.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.MemberDto;
import practice.jpastarter.exceptions.ResourceDuplicateException;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.delete.soft.SdMember;
import practice.jpastarter.repositories.delete.soft.SdMemberRepository;

import java.util.List;
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
    public Long createMember(String name, Integer age, String phone) {
        return memberRepository.findOneByPhoneWithDeleted(phone)
                .map(member -> {
                    if (!member.isDeleted()) {
                        throw new ResourceDuplicateException();
                    }
                    member.unDelete();
                    member.update(name, age, phone);
                    return member.getId();
                })
                .orElseGet(() -> {
                    SdMember newMember = memberRepository.save(new SdMember(name, age, phone));
                    return newMember.getId();
                });
    }

    @Override
    public MemberDto getSingleMember(Long memberId) {
        return memberRepository.findOneById(memberId)
                .map(MemberDto::new)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void updateMember(Long memberId, String name, Integer age, String phone) {
        SdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        member.update(name, age, phone);
    }

    @Override
    public void deleteMember(Long memberId) {
        SdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        member.delete();
    }
}
