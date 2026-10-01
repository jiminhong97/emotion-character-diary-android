package com.example.emotionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    ImageView imgCharacter;
    TextView tvTitle, tvDate, tvMoodSummary, tvEnergyLabel, tvMessage;
    TextView tvUserNote;
    RatingBar ratingDetail;
    Button btnToHome, btnToHistory;

    // 동적 배경 이미지 관리를 위한 별도의 ImageView 변수 2개를 선언했습니다.
    ImageView bgCurrent;
    ImageView bgNext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 뷰 연결
        imgCharacter   = findViewById(R.id.imgCharacter);
        tvTitle        = findViewById(R.id.tvTitle);
        tvDate         = findViewById(R.id.tvDate);
        tvMoodSummary  = findViewById(R.id.tvMoodSummary);
        tvEnergyLabel  = findViewById(R.id.tvEnergyLabel);
        tvMessage      = findViewById(R.id.tvMessage);
        tvUserNote     = findViewById(R.id.tvUserNote);
        ratingDetail   = findViewById(R.id.ratingDetail);
        btnToHome      = findViewById(R.id.btnToHome);
        btnToHistory   = findViewById(R.id.btnToHistory);
        bgCurrent = findViewById(R.id.bgCurrent);
        bgNext = findViewById(R.id.bgNext);

        // MainActivity에서 전달 받은 "오늘 날짜" 그대로 표시
        String date = getIntent().getStringExtra("date");
        tvDate.setText(date);

        // MainActivity에서 받은 데이터
        String mood = getIntent().getStringExtra("mood");
        float energy = getIntent().getFloatExtra("energy", 3);
        String userNote = getIntent().getStringExtra("note");
        boolean grown = getIntent().getBooleanExtra("grown", false); // ★ boolean 값에 따라 성장(big) 이미지 리소스를 조건부로 적용하는 메소드입니다.

        // 에너지 표시
        ratingDetail.setRating(energy);
        ratingDetail.setIsIndicator(true);
        tvEnergyLabel.setText("오늘의 에너지 : " + (int) energy + " / 5");

        // 감정 한글 텍스트 + 메시지
        String moodKorean = "";
        String message = "";
        boolean useBigImage = grown; // 성장 여부에 따라 BIG 이미지 사용

        if ("happy".equals(mood)) {
            moodKorean = "행복";
            imgCharacter.setImageResource(useBigImage ?
                    R.drawable.emotion_happy_big : R.drawable.emotion_happy);

            if (energy >= 4) {
                message = "오늘 감정씨는 햇살처럼 반짝이고 있어요 ☀\n\n기분 좋은 마음이 주변까지 퍼지고 있어요.";
            } else if (energy >= 2) {
                message = "오늘 감정씨는 조용히 미소 짓고 있어요 🙂\n\n큰 일은 없어도, 꽤 괜찮은 하루였어요.";
            } else {
                message = "조금 지친 하루 속에서도 작은 행복을 발견했어요.\n그것만으로도 감정씨는 충분히 기뻐하고 있어요 🌱";
            }

        } else if ("sad".equals(mood)) {
            moodKorean = "슬픔";
            imgCharacter.setImageResource(useBigImage ?
                    R.drawable.emotion_sad_big : R.drawable.emotion_sad);

            if (energy >= 4) {
                message = "마음이 무거웠지만, 그래도 오늘을 잘 버텨냈어요.\n감정씨가 조용히 옆에서 손을 잡고 있어요.";
            } else if (energy >= 2) {
                message = "살짝 축축한 하루였어요.\n눈물이 나도 괜찮아요. 감정씨가 다 보고 있었어요 🌧";
            } else {
                message = "오늘은 유난히 마음이 말랑했던 날이에요.\n내 마음을 지키기 위해 잠시 멈춰 서도 괜찮아요.";
            }

        } else if ("tired".equals(mood)) {
            moodKorean = "피곤";
            imgCharacter.setImageResource(useBigImage ?
                    R.drawable.emotion_tired_big : R.drawable.emotion_tired);

            if (energy >= 4) {
                message = "몸도 마음도 고생했지만, 끝까지 잘 버텼어요.\n감정씨가 이불로 감싸 안아주고 있어요 🛌";
            } else if (energy >= 2) {
                message = "하루 종일 힘이 빠지는 느낌이었죠.\n그래도 여기까지 온 나에게 감정씨가 박수 쳐줘요.";
            } else {
                message = "완전 방전된 하루였어요.\n지금은 아무것도 하지 않아도 괜찮은 시간이에요.";
            }

        } else if ("angry".equals(mood)) {
            moodKorean = "화남";
            imgCharacter.setImageResource(useBigImage ?
                    R.drawable.emotion_angry_big : R.drawable.emotion_angry);

            if (energy >= 4) {
                message = "오늘 감정씨는 꽤 불꽃 모드였어요 🔥\n화났던 마음도, 나를 지키기 위한 에너지였을 거예요.";
            } else if (energy >= 2) {
                message = "조금 예민해졌던 하루였어요.\n감정씨가 대신 불을 조금씩 식혀주고 있어요.";
            } else {
                message = "마음속에 작은 분노가 남아 있지만,\n이렇게 기록하는 순간부터 서서히 가라앉을 거예요.";
            }
        }

        // 결과 출력
        tvMoodSummary.setText("오늘 감정: " + moodKorean);
        tvMessage.setText(message);

        // 사용자 회고 문구
        if (userNote != null && !userNote.isEmpty()) {
            tvUserNote.setText("감정씨에게 남긴 말:\n" + userNote);
        } else {
            tvUserNote.setText("마음에 남긴 말:\n(오늘은 조용히 지나간 하루였어요)");
        }

        // 버튼 : 홈으로
        btnToHome.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, HomeActivity.class);
            // ★ 액티비티 스택 관리를 위해 FLAG_ACTIVITY_CLEAR_TOP을 사용하여, 홈으로 돌아갈 때 스택 위에 있는 모든 액티비티를 종료하는 기능을 추가했습니다.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // 버튼 : 히스토리로
        btnToHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }
    // 디테일 화면으로 돌아올 때마다 현재 기분 배경 적용
    // ★ 액티비티 생명주기 메소드 :
    // 화면이 다시 보일 때마다(Resume) 배경을 업데이트하는 로직입니다.
    @Override
    protected void onResume() {
        super.onResume();
        applyGlobalMoodToDetail();
    }

    // ★ 여기서부터 전역 "기분 배경" 적용 로직입니다.
    // 오늘 날짜 문자열 반환
    private String getToday() { // 날짜 포맷팅하는 메소드
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

    // 저장된 기분 배경을 디테일 화면 배경에 적용 (+ 날짜 바뀌면 초기화)
    private void applyGlobalMoodToDetail() { // SharedPreferences 활용해 날짜가 바뀌면 기분을 "default"로 초기화하고 배경 이미지를 동적으로 적용하는 핵심 로직입니다.
        SharedPreferences prefs = getSharedPreferences("EmotionData", MODE_PRIVATE);

        String today = getToday();
        String savedDate = prefs.getString("currentMoodDate", "");
        String mood = prefs.getString("currentMood", "default");

        // 날짜 바뀌면 초기화
        if (!today.equals(savedDate)) {
            mood = "default";
            prefs.edit() // SharedPreferences의 변경 사항을 비동기적으로 저장하는 apply() 메소드를 사용했습니다.
                    .putString("currentMood", "default")
                    .putString("currentMoodDate", today)
                    .apply();
        }

        // 배경 적용
        if (bgCurrent != null) bgCurrent.setImageResource(moodToBgRes(mood));
        if (bgNext != null) bgNext.setAlpha(0f);
    }
}
