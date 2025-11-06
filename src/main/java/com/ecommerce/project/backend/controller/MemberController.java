package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버
public class MemberController {

    private final MemberRepository memberRepository;

    // 회원가입 API
    @PostMapping("/signup")
    public String signup(@RequestBody Member member) {
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            return "이미 존재하는 이메일입니다.";
        }
        memberRepository.save(member);
        return "회원가입 성공";
    }

    // 로그인 API
    @PostMapping("/login")
    public String login(@RequestBody Member member) {
        return memberRepository.findByEmail(member.getEmail())
                .filter(m -> m.getPassword().equals(member.getPassword()))
                .map(m -> "로그인 성공")
                .orElse("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}

