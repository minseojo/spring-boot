package com.async.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AIImageService {

    private final RestTemplate restTemplate;

    public byte[] generateImage(String content) throws InterruptedException {
        // 실제 AI 이미지 생성 API 호출
        // 예시: byte[] image = restTemplate.postForObject("http://ai-image-service/generate", content, byte[].class);
        // 여기서는 임의의 바이트 배열을 반환
        Thread.sleep(5000); // 10초 걸린다는 가정
        return new byte[0];
    }
}
