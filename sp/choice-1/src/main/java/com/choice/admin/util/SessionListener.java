package com.choice.admin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.choice.admin.service.VisitorCountService;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
// 세션 리스너
public class SessionListener implements HttpSessionListener {

    @Autowired
    private VisitorCountService visitorCountService;

    @Override
    // 세션 생성 시 방문자 수 증가
    public void sessionCreated(HttpSessionEvent se) {
        visitorCountService.incrementCount();
    }

    @Override
    // 세션 종료 시 방문자 수 감소
    public void sessionDestroyed(HttpSessionEvent se) {
        visitorCountService.decrementCount();
    }
}
