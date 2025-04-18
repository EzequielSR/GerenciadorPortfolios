package com.example.GerenciadorPortfolios.controller;

import com.example.GerenciadorPortfolios.dto.MemberDto;
import com.example.GerenciadorPortfolios.service.ExternalMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external/members")
@RequiredArgsConstructor
@Tag(name = "External Members", description = "Mocked external API for members")
public class MemberController {
    private final ExternalMemberService externalMemberService;

    @PostMapping
    @Operation(summary = "Create a new member")
    public ResponseEntity<MemberDto> createMember(@RequestBody MemberDto memberDto) {
        return ResponseEntity.ok(externalMemberService.createMember(memberDto));
    }

    @GetMapping
    @Operation(summary = "Get all members")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(externalMemberService.findAllMembers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a member by ID")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        MemberDto member = externalMemberService.findMemberById(id);
        return member != null ? ResponseEntity.ok(member) : ResponseEntity.notFound().build();
    }
}