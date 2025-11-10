package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Member request) {
        String email = request.getEmail();
        String pw = request.getPassword();

        // ✅ 이메일 중복 검사
        if (memberRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body("❌ 이미 존재하는 이메일입니다.");
        }

        // ✅ 새 회원 저장
        Member newMember = new Member();
        newMember.setEmail(email);
        newMember.setPassword(pw);
        newMember.setRole("USER");

        memberRepository.save(newMember);
        return ResponseEntity.ok("✅ 회원가입 성공!");
    }
}

