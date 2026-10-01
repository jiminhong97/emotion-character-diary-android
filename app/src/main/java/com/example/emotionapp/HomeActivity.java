package com.example.emotionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnGuide;
    ImageButton btnRecord;
    ImageButton btnHistory;
    TextView tvDailyMessage;   // ★ 랜덤 힐링 문구

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnGuide = findViewById(R.id.btnGuide);
        btnRecord = findViewById(R.id.btnRecord);
        btnHistory = findViewById(R.id.btnHistory);
        tvDailyMessage = findViewById(R.id.tvDailyMessage);

        // ★ 랜덤 힐링 메시지 구현
        // : 문자열 배열을 선언하고.
        // Math.random() 함수를 사용하여 무작위로 인덱스를 생성해 메시지를 선택하는 로직을 설계했습니다.
        // 자바의 배열 및 난수 생성 기능을 활용해 직접 구현해 보았습니다.
        String[] healingMessages = { // 힐링 문구들은 LLM의 도움을 받아 선정했습니다.
                "오늘 하루도 감정씨가 함께 할게요 🌱",
                "잠깐 쉬어가도 괜찮아요. 감정씨가 보고 있어요 🍃",
                "지금 이 순간도 충분히 소중해요 ✨",
                "오늘의 마음, 감정씨가 조용히 안아줄게요 🤍",
                "작게라도 미소 지을 일이 생기길 바라요 ☀",
                "너무 무리하지 않아도 돼요. 천천히 가요 🌙"
        };

        // ★ 랜덤 문구 선택
        int index = (int)(Math.random() * healingMessages.length);
        tvDailyMessage.setText(healingMessages[index]);

        // "무슨 어플인가요?" 버튼 클릭
        btnGuide.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GuideActivity.class);
            startActivity(intent);
        });

        // "기록할래요!" 버튼 클릭
        btnRecord.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // "히스토리" 버튼 클릭
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

    }

    // 홈 화면으로 돌아올 때마다 현재 기분 배경 적용
    @Override
    protected void onResume() {
        super.onResume();
        applyGlobalMoodToHome();
    }

    // 오늘 날짜 문자열 반환
    private String getToday() {
        long now = System.currentTimeMillis();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date(now));
    }

    // mood 문자열 → 배경 drawable 매핑
    private int moodToBgRes(String mood) {
        switch (mood) {
            case "happy": return R.drawable.bg_happy;
            case "sad": return R.drawable.bg_sad;
            case "tired": return R.drawable.bg_tired;
            case "angry": return R.drawable.bg_angry;
            default: return R.drawable.bg_default;
        }
    }

    // 저장된 기분 배경을 홈 화면 배경에 적용 (+ 날짜 바뀌면 초기화)
    private void applyGlobalMoodToHome() {
        SharedPreferences prefs = getSharedPreferences("EmotionData", MODE_PRIVATE);

        String today = getToday();
        String savedDate = prefs.getString("currentMoodDate", "");
        String mood = prefs.getString("currentMood", "default");

        // 날짜 바뀌면 초기화
        if (!today.equals(savedDate)) {
            mood = "default";
            prefs.edit()
                    .putString("currentMood", "default")
                    .putString("currentMoodDate", today)
                    .apply();
        }

        findViewById(R.id.rootLayout).setBackgroundResource(moodToBgRes(mood));
    }
}
