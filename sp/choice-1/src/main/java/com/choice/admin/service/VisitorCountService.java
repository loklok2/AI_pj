package com.choice.admin.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
// 방문자 수 서비스
public class VisitorCountService {
    // 현재 활성 세션 수
    private int activeSessionCount = 0;
    // 일일 방문자 수
    private int dailyVisitorCount = 0;

    // 방문자 수 증가
    public synchronized void incrementCount() {
        activeSessionCount++;
        dailyVisitorCount++;
    }

    // 방문자 수 감소
    public synchronized void decrementCount() {
        if (activeSessionCount > 0) {
            activeSessionCount--;
        }
    }

    // 현재 활성 세션 수 반환
    public int getActiveSessionCount() {
        return activeSessionCount;
    }

    // 일일 방문자 수 반환
    public int getDailyVisitorCount() {
        return dailyVisitorCount;
    }

    // 일일 방문자 수 초기화
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void resetDailyCount() {
        dailyVisitorCount = 0;
    }
}
