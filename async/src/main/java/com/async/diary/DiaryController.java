package com.async.diary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @PostMapping
    public DeferredResult<String> createDiary(@RequestBody String content) {
        DeferredResult<String> deferredResult = new DeferredResult<>();

        // 비동기 작업 수행
        diaryService.createDiary(content, deferredResult);

        return deferredResult;
    }

    @GetMapping
    public List<DiaryResponse> showDiaries() {
        return diaryService.getDiaries();
    }
}
