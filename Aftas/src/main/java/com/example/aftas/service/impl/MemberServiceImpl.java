package com.example.aftas.service.impl;

import com.example.aftas.model.Member;
import com.example.aftas.repository.MemberRepository;
import com.example.aftas.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new RuntimeException("Member with id " + id + " not found"));
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member addMember(Member member) {

        if (memberRepository.existsByIdentityNumber(member.getIdentityNumber())) {
            throw new RuntimeException("IdentityNumber already exists");
        }

        member.setAccessDate(java.time.LocalDate.now());

//        member.setIdentityNumber(java.util.UUID.randomUUID().toString());

        member.setMembershipNumber((int) (memberRepository.count() + 1));
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findByMembershipNumberOrIdentityNumberOrNameOrFamilyName(String searchTerm) {
        return memberRepository.findByMembershipNumberOrIdentityNumberOrNameOrFamilyName(searchTerm);
    }

    @Override
    public Member updateMember(Member member, Long id) {

        if (memberRepository.existsByIdentityNumber(member.getIdentityNumber())) {
            throw new RuntimeException("IdentityNumber already exists");
        }

        Member existingMember = getMemberById(id);
        existingMember.setName(member.getName());
        existingMember.setFamilyName(member.getFamilyName());
        existingMember.setNationality(member.getNationality());
        existingMember.setIdentityDocumentType(member.getIdentityDocumentType());
        existingMember.setIdentityNumber(member.getIdentityNumber());
        return memberRepository.save(existingMember);
    }

    @Override
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
