package kr.adapterz.community.domain.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("health")
public class Health {
    @GetMapping
    String checkHealth() {
        return "OK";
    }
}
