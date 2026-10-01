package com.example.emotionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ViewFlipper;

public class GuideActivity extends AppCompatActivity {

    ViewFlipper guideFlipper;

    // ★ 터치 이벤트 좌표 저장
    // : 화면 터치 이벤트를 처리하기 위해 터치 시작점의 X 좌표를 저장하는 변수를 직접 선언했습니다.
    float startX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        guideFlipper = findViewById(R.id.guideFlipper);

        Button btnHome = findViewById(R.id.btnGuideHome);

        // 마지막 페이지에서 "홈으로 돌아가기" 버튼 클릭
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(GuideActivity.this, HomeActivity.class));
            finish();
        });
    }

    // 화면을 좌우로 스와이프해서 페이지 넘기기
    // ★ 터치 이벤트 처리 (스와이프 제스처 구현)
    // : onTouchEvent() 메소드를 오버라이딩하여 터치 다운(ACTION_DOWN)과 터치 업(ACTION_UP) 이벤트의 X 좌표 차이를 계산해 좌우 스와이프 제스처를 감지하는 로직을 구현했습니다.
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                return true;

            case MotionEvent.ACTION_UP:
                float endX = event.getX();

                // ★ 스와이프 민감도(Threshold) 설정
                // : 단순한 좌우 이동이 아닌, 150픽셀 이상의 이동 거리가 발생했을 때만 페이지를 넘기도록 임계값(Threshold)을 설정했습니다.
                if (startX > endX + 150) {
                    // 왼쪽 방향 → 다음 페이지
                    guideFlipper.showNext();
                } else if (startX < endX - 150) {
                    // 오른쪽 방향 → 이전 페이지
                    guideFlipper.showPrevious();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
