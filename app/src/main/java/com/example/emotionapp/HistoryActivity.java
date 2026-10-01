package com.example.emotionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

// ★ JSON 데이터 처리 라이브러리 활용
// : Android 내장 JSON 라이브러리로 문자열 형태의 기록 데이터를 객체/배열 형태로 파싱하고 처리했습니다.
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView tvDate, tvResultMood, tvResultEnergy, tvResultNote;
    ImageView imgResult;
    Button btnToHome;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        calendarView = findViewById(R.id.calendarView);
        tvDate = findViewById(R.id.tvDate);
        tvResultMood = findViewById(R.id.tvResultMood);
        tvResultEnergy = findViewById(R.id.tvResultEnergy);
        tvResultNote = findViewById(R.id.tvResultNote);
        imgResult = findViewById(R.id.imgResult);
        btnToHome = findViewById(R.id.btnToHome);

        // 날짜 선택 이벤트
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                // ★ 날짜 포맷팅 (String.format)
                // : CalendarView에서 받은 날짜 정보를 일관된 형식(YYYY-MM-DD)으로 만들기 위해
                // String.format()과 %02d와 같은 정밀한 포맷 지정자를 사용했습니다.
                String dateStr = String.format("%04d-%02d-%02d",
                        year, month + 1, dayOfMonth);

                tvDate.setText(dateStr + "의 감정 기록");

                loadEmotionForDate(dateStr);
            }
        });
        // "홈으로 돌아가기" 버튼 클릭
        btnToHome.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    // 히스토리 화면으로 돌아올 때마다 현재 기분 배경 적용
    @Override
    protected void onResume() {
        super.onResume();
        applyGlobalMoodToHistory();
    }

    // ★ SharedPreferences에 JSON 배열 저장 및 로드
    // : 단순한 키-값 쌍(String, Int 등)을 저장하고,
    // 복잡한 데이터 구조(기록 배열)를 JSON 문자열로 직렬화하여 저장하고,
    // 이를 다시 JSONArray, JSONObject로 역직렬화하여 읽어오는 로직을 사용했습니다.
    private void loadEmotionForDate(String date) {
        SharedPreferences prefs = getSharedPreferences("EmotionData", MODE_PRIVATE);
        String jsonString = prefs.getString("records", "[]");

        try {
            JSONArray arr = new JSONArray(jsonString);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                if (obj.getString("date").equals(date)) {

                    String mood = obj.getString("mood");
                    float energy = (float) obj.getDouble("energy");
                    String note = obj.optString("note", "");
                    boolean grown = obj.optBoolean("grown", false);  // ★ 성장 여부 읽기

                    // 감정 이미지 표시 (grown에 따라 BIG / 기본 선택)
                    switch (mood) {
                        case "happy":
                            imgResult.setImageResource(
                                    grown ? R.drawable.emotion_happy_big : R.drawable.emotion_happy
                            );
                            break;
                        case "sad":
                            imgResult.setImageResource(
                                    grown ? R.drawable.emotion_sad_big : R.drawable.emotion_sad
                            );
                            break;
                        case "tired":
                            imgResult.setImageResource(
                                    grown ? R.drawable.emotion_tired_big : R.drawable.emotion_tired
                            );
                            break;
                        case "angry":
                            imgResult.setImageResource(
                                    grown ? R.drawable.emotion_angry_big : R.drawable.emotion_angry
                            );
                            break;
                    }

                    imgResult.setVisibility(View.VISIBLE);

                    String moodKor = "";

                    // 감정을 한글로 표시 하도록 설정
                    if (mood.equals("happy")) moodKor = "행복";
                    else if (mood.equals("sad")) moodKor = "슬픔";
                    else if (mood.equals("tired")) moodKor = "피곤";
                    else if (mood.equals("angry")) moodKor = "화남";

                    tvResultMood.setText("감정 : " + moodKor);

                    tvResultMood.setVisibility(View.VISIBLE);

                    tvResultEnergy.setText("에너지 : " + energy);
                    tvResultEnergy.setVisibility(View.VISIBLE);

                    tvResultNote.setText("감정씨에게 : " + note);
                    tvResultNote.setVisibility(View.VISIBLE);

                    return;
                }
            }

            // 기록 없을 때
            imgResult.setVisibility(View.GONE);
            tvResultMood.setVisibility(View.GONE);
            tvResultEnergy.setVisibility(View.GONE);
            tvResultNote.setVisibility(View.GONE);
            tvDate.setText(date + " : 기록 없음");

        } catch (JSONException e) { // ★ 특정 예외 처리
            e.printStackTrace();
            // : JSON 파싱 과정에서 발생할 수 있는 JSONException을 명시적으로 잡아내서 처리하는 코드를 적용했습니다.
        }
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

    // 저장된 기분 배경을 히스토리 배경에 적용 (+ 날짜 바뀌면 초기화)
    private void applyGlobalMoodToHistory() {
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

        // ★ 동적 배경 리소스 적용
        // : 현재 기분 상태에 따라 moodToBgRes() 함수를 호출하여 배경 이미지 리소스를 가져오고,
        // setBackgroundResource()를 사용하여 동적으로 레이아웃 배경을 변경하도록 적용했습니다.
        findViewById(R.id.rootLayout).setBackgroundResource(moodToBgRes(mood));
    }
}
