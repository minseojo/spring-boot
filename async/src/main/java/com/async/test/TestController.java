package com.async.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/async")
    public void home() {
        System.out.println("컨트롤러 호출");
        testService.publicAsyncMethod();
        System.out.println("컨트롤러 리턴");
    }
}
