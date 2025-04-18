package com.example.GerenciadorPortfolios.service;

import com.example.GerenciadorPortfolios.dto.MemberDto;
import com.example.GerenciadorPortfolios.model.Member;
import com.example.GerenciadorPortfolios.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalMemberService {
    private final MemberRepository memberRepository;

    public MemberDto createMember(MemberDto memberDto) {
        Member member = new Member();
        member.setName(memberDto.getName());
        member.setRole(memberDto.getRole());
        member.setExternalId(System.currentTimeMillis()); // Simulate external ID

        Member savedMember = memberRepository.save(member);
        return convertToDto(savedMember);
    }

    public List<MemberDto> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MemberDto findMemberById(Long id) {
        return memberRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    private MemberDto convertToDto(Member member) {
        MemberDto dto = new MemberDto();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setRole(member.getRole());
        return dto;
    }
}