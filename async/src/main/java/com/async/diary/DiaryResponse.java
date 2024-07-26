package com.async.diary;

public record DiaryResponse(String content, String imageUrl) {

    public static DiaryResponse of(Diary diary) {
        return new DiaryResponse(diary.getContent(), diary.getImageUrl());
    }
}
