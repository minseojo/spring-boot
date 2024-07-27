package com.async.test;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Async
    public void publicAsyncMethod() {
        System.out.println("비동기 메서드 실행");
        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                System.out.println("비동기 메서드 실행중 " + i + "초");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        System.out.println("비동기 메서드 종료");
    }

// 에러
//    @Async
//    private void privateAsyncMethod() {
//
//    }
}
