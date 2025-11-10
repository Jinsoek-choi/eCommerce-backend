package com.ecommerce.project.backend.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")  // 프론트엔드 주소 허용
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 세션이 존재하면 무효화
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 성공");
    }
}
