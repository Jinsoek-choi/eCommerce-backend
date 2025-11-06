package com.ecommerce.project.backend.controller;

import com.ecommerce.project.backend.dto.LoginRequest;
import com.ecommerce.project.backend.domain.Member;
import com.ecommerce.project.backend.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 연동 허용
public class LoginController {

    private final MemberRepository memberRepository;

    public LoginController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String id = request.getId();
        String pw = request.getPw();

        // findByEmail()은 Optional을 반환하므로 get() 또는 orElseThrow()로 처리
        Member member = memberRepository.findByEmail(id)
                .orElse(null); // 존재하지 않으면 null 반환

        if (member == null) {
            return ResponseEntity.status(404).body("존재하지 않는 사용자입니다.");
        }

        if (!member.getPassword().equals(pw)) {
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok("로그인 성공: " + member.getEmail());
    }

}

