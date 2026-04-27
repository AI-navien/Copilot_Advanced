# 2. Instruction MD 가이드

## 학습 목표
- GitHub Copilot의 Instruction 기능 이해
- 조직 단위 / 개인 단위 Instruction 작성법 습득
- Instruction을 통한 코드 품질 향상 방법 학습

---

## 교육 내용

### 2.1 Copilot Instruction이란?

Copilot Instruction은 AI가 코드 제안 시 참고하는 **맥락 정보**입니다.

#### 종류
| 구분 | 파일 위치 | 용도 |
|------|-----------|------|
| 조직 단위 | `.github/copilot-instructions.md` | 프로젝트 전체 공통 규칙 |
| 개인 단위 | 개인 설정 | 개인 선호 스타일 |

### 2.2 조직 단위 Instruction 작성법

본 프로젝트의 `.github/copilot-instructions.md` 파일을 참고하세요.

#### 포함해야 할 내용

**1. 프로젝트 개요**
```markdown
## 프로젝트 개요
- 프로젝트명: MSA E-Commerce
- 기술 스택: Java 17, Spring Boot 3.2, Kafka
```

**2. 코딩 컨벤션**
```markdown
## 코딩 컨벤션
- 클래스명: PascalCase
- 메서드명: camelCase
- REST API는 복수형 명사 사용 (/users, /products)
```

**3. 패키지 구조**
```markdown
## 패키지 구조
controller/ → REST API
service/    → 비즈니스 로직
repository/ → 데이터 접근
```

**4. 테스트 가이드**
```markdown
## 테스트 규칙
- 단위 테스트: JUnit 5 + Mockito
- 메서드명: should_결과_when_조건
```

### 2.3 효과적인 Instruction 작성 팁

#### ✅ DO (권장)
- 구체적인 기술 스택 명시
- 명확한 네이밍 규칙
- 예시 코드 포함
- 에러 처리 방식 정의

#### ❌ DON'T (비권장)
- 너무 길고 복잡한 내용
- 모호한 표현
- 상충되는 규칙

### 2.4 Instruction 적용 확인

Instruction이 잘 적용되는지 테스트하는 방법:

```java
// UserController.java 파일에서
// "사용자 목록을 조회하는 API를 추가해주세요" 라고 입력

// Instruction이 적용되면:
// - @GetMapping 사용
// - ApiResponse<List<UserResponse>> 반환
// - camelCase 메서드명
```

---

## 실습

### 실습 1: Instruction 분석
프로젝트의 `.github/copilot-instructions.md` 파일을 열어보고:
1. 어떤 규칙들이 정의되어 있는지 확인
2. 누락된 규칙이 있다면 추가해보기

### 실습 2: Instruction 효과 체험
1. Instruction 내용 일부를 삭제
2. 같은 프롬프트로 코드 생성 요청
3. 결과물 품질 비교

---

## 핵심 포인트
1. **일관된 코드 품질**을 위해 Instruction 필수
2. **프로젝트 초기**에 Instruction 세팅
3. **팀 합의**를 통해 Instruction 내용 결정
4. **주기적 업데이트**로 Instruction 개선

