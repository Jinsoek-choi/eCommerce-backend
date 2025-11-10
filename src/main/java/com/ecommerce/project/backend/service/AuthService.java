package com.ecommerce.project.backend.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public void logout(HttpSession session) {
        if (session != null) session.invalidate();
    }
}

