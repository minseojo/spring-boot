package com.async.diary;

import com.async.ai.AIImageService;
import com.async.file.LocalFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.Future;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private AIImageService aiImageService;

    @Autowired
    private LocalFileService localFileService;

    @Async
    public Future<String> createDiary(String content, DeferredResult<String> deferredResult) {
        try {
            // 1. AI 서비스로부터 이미지 생성
            byte[] imageData = aiImageService.generateImage(content);

            // 2. 로컬 파일 시스템에 이미지 저장
            String imagePath = localFileService.saveImage(imageData);

            // 3. 데이터베이스에 저장
            Diary diary = new Diary(content, imagePath);
            diaryRepository.save(diary);

            // 4. 클라이언트에 이미지 경로 반환
            deferredResult.setResult(imagePath);

            return new AsyncResult<>(imagePath);
        } catch (Exception e) {
            deferredResult.setErrorResult(e.getMessage());
            return new AsyncResult<>(null);
        }
    }

    public List<DiaryResponse> getDiaries() {
        return diaryRepository.findAll()
                .stream()
                .map(DiaryResponse::of)
                .toList();
    }
}
