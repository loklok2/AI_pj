package com.choice.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.choice.admin.service.VisitorCountService;

@RestController
@RequestMapping("/api/visitors")
// 방문자 수 조회
public class VisitorCountController {

    @Autowired
    private VisitorCountService visitorCountService;

    @GetMapping("/count")
    // 방문자 수 조회
    public int getVisitorCount() {
        return visitorCountService.getActiveSessionCount();
    }
}
