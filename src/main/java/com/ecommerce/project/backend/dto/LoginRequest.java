package com.ecommerce.project.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String id;  // 프론트엔드에서 보낸 "id"
    private String pw;  // 프론트엔드에서 보낸 "pw"
}

