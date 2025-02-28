package com.study.library.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthCheckController {

    @Value("${server.name}")
    private String serverName;

    @GetMapping("/server/health") //서버의 구동상태 확인
    public ResponseEntity<?> check() {
        return ResponseEntity.ok(Map.of("serverName", serverName));
    }
}
