package practice.jpastarter.services.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.jpastarter.dtos.MemberDto;
import practice.jpastarter.exceptions.GenerateUUIDException;
import practice.jpastarter.exceptions.ResourceDuplicateException;
import practice.jpastarter.exceptions.ResourceNotFoundException;
import practice.jpastarter.models.delete.hard.HdMember;
import practice.jpastarter.repositories.delete.hard.HdMemberRepository;

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
public class HdMemberService implements MemberService {
    private final HdMemberRepository memberRepository;

    @Transactional
    @Override
    public Long createMember(MemberDto memberDto) {
        // 중복 검사
        if (memberRepository.findOneByPhone(memberDto.getPhone()).isPresent()) {
            throw new ResourceDuplicateException();
        }
        HdMember member = memberRepository.save(
                new HdMember(generateUUID(), memberDto.getName(), memberDto.getAge(), memberDto.getPhone())
        );
        return member.getId();
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

    @Transactional
    @Override
    public void updateMember(Long memberId, MemberDto memberDto) {
        HdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        member.update(member.getName(), memberDto.getAge(), memberDto.getPhone());
    }

    @Transactional
    @Override
    public void deleteMember(Long memberId) {
        HdMember member = memberRepository.findOneById(memberId)
                .orElseThrow(ResourceNotFoundException::new);
        memberRepository.delete(member);
    }

    private String generateUUID() {
        final int retryCount = 10;
        for (int i = 0; i < retryCount; i++) {
            String uuid = UUID.randomUUID().toString();
            if (memberRepository.findOneByUuid(uuid).isEmpty()) {
                return uuid;
            }
        }
        throw new GenerateUUIDException();
    }
}
