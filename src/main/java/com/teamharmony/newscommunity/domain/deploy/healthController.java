package com.teamharmony.newscommunity.domain.deploy;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * EB와의 헬스체크를 위해
 * @Author chanhyeoKing
 */
@RequestMapping("/actuator/health")
public class healthController {

    @GetMapping("")
    public int healthcheck(){
        return 200;
    }
}
