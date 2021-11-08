package practice.jpastarter.services.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.MemberDto;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.repositories.delete.hard.HdMemberRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2021/11/08
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HdMemberService implements MemberService {
    private final HdMemberRepository memberRepository;

    @Transactional
    @Override
    public Long createMember(String name, Integer age, String phone) {
        HdMember member = memberRepository.save(new HdMember(name, age, phone));
        return member.getId();
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

    @Transactional
    @Override
    public void updateMember(Long memberId, String name, Integer age, String phone) {
        HdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        member.update(name, age, phone);
    }

    @Transactional
    @Override
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
