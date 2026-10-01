package com.example.emotionapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    RadioGroup rgMood;
    RatingBar ratingBar;
    ImageView imgEmotion;

    // 배경 전환용 이미지 뷰 2개
    ImageView bgCurrent;
    ImageView bgNext;

    Button btnSave;
    String selectedMood = "happy"; // 기본 감정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearGlobalMoodIfNewDay(); // 앱 들어올 때 “날짜가 바뀌었으면 감정 배경 초기화”

        // UI 연결
        rgMood = findViewById(R.id.rgMood);
        ratingBar = findViewById(R.id.ratingBar);
        imgEmotion = findViewById(R.id.imgEmotion);

        bgCurrent = findViewById(R.id.bgCurrent);
        bgNext = findViewById(R.id.bgNext);

        btnSave = findViewById(R.id.btnSave);

        // 라디오 버튼 선택 시 캐릭터&배경 변경
        rgMood.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbHappy) {
                selectedMood = "happy";
                saveGlobalMood(selectedMood);
                imgEmotion.setImageResource(R.drawable.emotion_happy);
                changeBackgroundSmoothly(R.drawable.bg_happy);

            } else if (checkedId == R.id.rbSad) {
                selectedMood = "sad";
                saveGlobalMood(selectedMood);
                imgEmotion.setImageResource(R.drawable.emotion_sad);
                changeBackgroundSmoothly(R.drawable.bg_sad);

            } else if (checkedId == R.id.rbTired) {
                selectedMood = "tired";
                saveGlobalMood(selectedMood);
                imgEmotion.setImageResource(R.drawable.emotion_tired);
                changeBackgroundSmoothly(R.drawable.bg_tired);

            } else if (checkedId == R.id.rbAngry) {
                selectedMood = "angry";
                saveGlobalMood(selectedMood);
                imgEmotion.setImageResource(R.drawable.emotion_angry);
                changeBackgroundSmoothly(R.drawable.bg_angry);
            }
        });

        // 저장 버튼 → 확인 다이얼로그 → 회고 입력 다이얼로그로 연결
        btnSave.setOnClickListener(v -> {

            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
            dlg.setTitle("감정씨에게 저장할까요?");
            dlg.setMessage("오늘의 감정을 감정씨에게 알려줄까요?");

            dlg.setPositiveButton("네", (dialog, which) -> showWriteDialog());
            dlg.setNegativeButton("아니요", null);
            dlg.show();
        });
    }

    // 감정씨에게 한 줄 말걸기 다이얼로그
    private void showWriteDialog() {

        final EditText input = new EditText(MainActivity.this);
        input.setHint("오늘 감정씨에게 하고 싶은 말을 적어볼까요?");

        AlertDialog.Builder writeDlg = new AlertDialog.Builder(MainActivity.this);
        writeDlg.setTitle("감정씨에게 한 마디 🍃");
        writeDlg.setView(input);

        writeDlg.setPositiveButton("전할게요", (dialog, which) -> {

            String userNote = input.getText().toString();
            if (userNote.trim().isEmpty()) {
                userNote = "오늘 감정을 감정씨에게 살짝 맡겨두었어요.";
            }

            float energy = ratingBar.getRating();

            // 기록 + 성장 규칙 적용 후 today 반환 받기
            SaveResult result = saveEmotionRecord(selectedMood, energy, userNote);

            // DetailActivity로 이동
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("mood", selectedMood);
            intent.putExtra("energy", energy);
            intent.putExtra("note", userNote);
            intent.putExtra("grown", result.grown);
            intent.putExtra("date", result.date);   // ★ 오늘 날짜 전달
            startActivity(intent);
        });

        writeDlg.setNegativeButton("건너뛰기", (dialog, which) -> {

            String userNote = "오늘 감정을 조용히 기록해 두었어요.";
            float energy = ratingBar.getRating();

            SaveResult result = saveEmotionRecord(selectedMood, energy, userNote);

            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("mood", selectedMood);
            intent.putExtra("energy", energy);
            intent.putExtra("note", userNote);
            intent.putExtra("grown", result.grown);
            intent.putExtra("date", result.date);
            startActivity(intent);
        });

        writeDlg.show();
    }

    // 저장한 날짜 + grown 여부를 함께 돌려 받기 위한 클래스
    private static class SaveResult {
        String date;
        boolean grown;

        SaveResult(String d, boolean g) {
            date = d;
            grown = g;
        }
    }

    // 감정 기록 저장 + 성장 규칙 적용
    // ★ 핵심 성장 규칙 로직 및 객체 반환
    // : 성장 규칙 함수를 정의하고,
    // 저장된 날짜와 성장 여부(grownToday)를 별도의 클래스(SaveResult)를 통해 객체 형태로 반환하도록 했습니다.
    private SaveResult saveEmotionRecord(String mood, float energy, String note) {

        boolean grownToday = false;
        String today = "";

        try {
            // 날짜 계산
            long now = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            today = sdf.format(new Date(now));

            // ★ SharedPreferences에 감정 저장
            SharedPreferences prefs = getSharedPreferences("EmotionData", MODE_PRIVATE);
            String jsonString = prefs.getString("records", "[]");

            JSONArray arr = new JSONArray(jsonString);

            // ★ 이미 오늘 저장된 기록이 있으면 삭제 → 최신 기록으로 대체
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("date").equals(today)) {
                    arr.remove(i);
                    break;
                }
            }

            // 성장 규칙 계산
            int consecutive = 1;

            String yesterday = getOtherDay(-1);
            String dayBefore = getOtherDay(-2);

            if (isSameMoodRecord(arr, yesterday, mood)) consecutive++;
            if (isSameMoodRecord(arr, dayBefore, mood)) consecutive++;
            if (consecutive >= 3) grownToday = true;

            // 오늘 기록 추가
            JSONObject obj = new JSONObject();
            obj.put("date", today);
            obj.put("mood", mood);
            obj.put("energy", energy);
            obj.put("note", note);
            obj.put("grown", grownToday);

            arr.put(obj);

            prefs.edit().putString("records", arr.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new SaveResult(today, grownToday);
    }

    // ★ 날짜 계산 로직 (성장 규칙의 핵심)
    // : 특정 날짜(diff를 통해 어제, 그제 등)를 계산하기 위해
    // System.currentTimeMillis()에 하루의 밀리초를 더하거나 빼는 로직을 구현했습니다.
    // → 날짜 계산 및 3일 연속 감정 체크 로직
    private String getOtherDay(int diff) {
        long base = System.currentTimeMillis() + diff * 24L * 60L * 60L * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(base));
    }

    private boolean isSameMoodRecord(JSONArray arr, String targetDate, String targetMood) throws JSONException {
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("date").equals(targetDate)) {
                return obj.getString("mood").equals(targetMood);
            }
        }
        return false;
    }

    // ★ 배경 전환 애니메이션
    private void changeBackgroundSmoothly(int newBgResId) {

        bgNext.setImageResource(newBgResId); // 1. 다음 배경 이미지 리소스를 bgNext에 설정
        bgNext.setAlpha(0f); // 2. bgNext의 투명도를 0으로 설정 (안 보이도록)

        bgNext.animate()
                .alpha(1f) // 3. 0이었던 투명도를 1(불투명)로 700ms 동안 애니메이션
                .setDuration(700)
                .withEndAction(() -> {
                    // 4. 애니메이션이 끝난 후 실행될 동작 (람다식)
                    bgCurrent.setImageDrawable(bgNext.getDrawable()); // bgCurrent를 bgNext의 이미지로 교체
                    bgNext.setAlpha(0f); // bgNext는 다시 투명하게 대기 상태로 복귀
                })
                .start();
    }
    private String getToday() {
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(now));
    }

    private void saveGlobalMood(String mood) {
        SharedPreferences prefs = getSharedPreferences("EmotionData", MODE_PRIVATE);
        prefs.edit()
                .putString("currentMood", mood)
                .putString("currentMoodDate", getToday())
                .apply();
    }

    private void clearGlobalMoodIfNewDay() {
        SharedPreferences prefs = getSharedPreferences("EmotionData", MODE_PRIVATE);
        String savedDate = prefs.getString("currentMoodDate", "");
        String today = getToday();

        if (!today.equals(savedDate)) {
            prefs.edit()
                    .putString("currentMood", "default")
                    .putString("currentMoodDate", today)
                    .apply();
        }
    }

}
