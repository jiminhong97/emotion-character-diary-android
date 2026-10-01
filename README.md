# Emotion-Based Character Growth Diary App

사용자가 하루의 감정과 에너지를 기록하면 캐릭터와 배경이 감정 상태에 따라 변화하고, 기록 패턴에 따라 캐릭터가 성장하도록 구현한 Android 감정 일기 애플리케이션입니다.

모바일웹프로그래밍 과목의 개인 기말 프로젝트로 진행하였으며, Java와 XML을 이용하여 6개의 Activity와 각 화면의 레이아웃을 직접 구현하였습니다.

## 프로젝트 아이디어

하루의 감정을 단순 텍스트로만 기록하는 대신, **감정을 캐릭터로 형상화하고 반복되는 감정 패턴을 캐릭터 성장으로 표현**하는 것을 목표로 하였습니다.

사용자는 다음 정보를 기록할 수 있습니다.

- 감정: 행복 / 슬픔 / 피곤 / 화남
- 에너지: RatingBar 기반 1~5 단계
- 하루 회고 문구

저장된 감정에 따라 캐릭터 이미지와 앱 배경이 바뀌며, 같은 감정을 3일 연속 기록하면 해당 감정 캐릭터가 성장한 모습으로 표시됩니다.

## 화면 구성

```text
Intro
  ↓
Home
  ├── Guide
  ├── Emotion Record
  │      ↓
  │    Detail
  │
  └── History
```

총 6개의 Java Activity와 6개의 XML 레이아웃으로 구성하였습니다.

| Activity | 역할 |
| --- | --- |
| `IntroActivity` | 2초 Splash 화면 후 Home으로 이동 |
| `HomeActivity` | Guide / 감정 기록 / History 화면으로 이동, 랜덤 힐링 문구 표시 |
| `GuideActivity` | ViewFlipper와 터치 이벤트를 이용한 좌우 Swipe Guide |
| `MainActivity` | 감정·에너지·회고 입력, JSON 저장, 캐릭터 성장 판정 |
| `DetailActivity` | 기록 결과와 감정·에너지 조합에 따른 메시지 표시 |
| `HistoryActivity` | CalendarView에서 날짜를 선택해 과거 감정 기록 조회 |

## 주요 구현 기능

### 1. 감정 및 에너지 기록

`RadioGroup`으로 감정을 선택하고 `RatingBar`로 그날의 에너지를 기록합니다. 감정을 선택하면 캐릭터와 배경 이미지가 즉시 변경됩니다.

### 2. SharedPreferences + JSON 영속 저장

단순 값만 저장하는 대신 감정 기록을 JSON 객체로 구성한 뒤 `JSONArray` 형태로 직렬화하여 `SharedPreferences`에 저장합니다.

저장하는 주요 값:

```text
date
mood
energy
note
grown
```

같은 날짜에 다시 기록하면 기존 기록을 제거하고 최신 기록으로 갱신하도록 구현하였습니다.

### 3. 패턴 기반 캐릭터 성장

오늘과 직전 2일의 기록을 확인하여 **동일한 감정을 3일 연속 기록한 경우** `grown=true`로 저장합니다.

이 값에 따라 Detail 및 History 화면에서 기본 캐릭터와 성장 캐릭터 이미지를 구분하여 표시합니다.

### 4. 감정에 따른 동적 UI

현재 감정 상태를 전역 상태처럼 저장하고 Home, Main, Detail, History 화면의 배경에 동일하게 반영합니다.

날짜가 바뀌면 저장된 현재 감정 상태를 `default`로 초기화합니다.

Main 화면에서는 두 개의 ImageView를 이용해 새로운 배경을 700ms 동안 Fade-in시키는 전환 효과를 구현하였습니다.

### 5. Calendar 기반 History

`CalendarView`에서 날짜를 선택하면 해당 날짜의 JSON 기록을 검색하여 감정, 에너지, 회고 문구와 성장 상태를 복원합니다.

### 6. 추가 UI 및 이벤트 처리

- `Intent`를 이용한 Activity 간 데이터 전달
- `FLAG_ACTIVITY_CLEAR_TOP`을 이용한 Activity stack 관리
- `AlertDialog` 기반 저장 확인 및 회고 입력
- `ViewFlipper`와 TouchEvent를 이용한 Guide Swipe
- 랜덤 힐링 메시지 출력
- 감정과 에너지 조합에 따른 결과 메시지 분기

## 사용 기술

- Java
- XML
- Android Studio
- Android SDK
- SharedPreferences
- JSON (`JSONArray`, `JSONObject`)
- Material / AppCompat

## 프로젝트 구조

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/emotionapp/
│   ├── IntroActivity.java
│   ├── HomeActivity.java
│   ├── GuideActivity.java
│   ├── MainActivity.java
│   ├── DetailActivity.java
│   └── HistoryActivity.java
└── res/
    └── layout/
        ├── activity_intro.xml
        ├── activity_home.xml
        ├── activity_guide.xml
        ├── activity_main.xml
        ├── activity_detail.xml
        └── activity_history.xml
```

## Android 설정

- Min SDK: 24
- Target SDK: 36
- Compile SDK: 36
- Java Compatibility: Java 11
- Android Gradle Plugin: 8.12.3

## 공개 저장소 참고사항

본 저장소는 프로그래밍 구현을 확인할 수 있도록 Java 및 XML 소스코드를 중심으로 정리한 포트폴리오 버전입니다.

로컬 개발환경에서 자동 생성되는 `.gradle/`, `.idea/`, `build/`, `local.properties`는 제외하였습니다. 캐릭터·배경 이미지와 커스텀 폰트 등 바이너리 디자인 리소스 역시 공개 포트폴리오에서는 제외하였으므로, 원본 UI 디자인을 그대로 빌드하기보다는 코드 구조와 구현 로직을 확인하기 위한 저장소입니다.

## 프로젝트 정보

- 수행 형태: 모바일웹프로그래밍 개인 기말 프로젝트
- 개발 환경: Android Studio
- 주요 언어: Java, XML
