# common

Michelet MSA 전체 서비스가 공유하는 **공통 모듈(Java Library)**.

---

## 포함 내용

| 패키지 | 구성 요소 | 설명 |
|---|---|---|
| `com.michelet.common.response` | `ApiResponse<T>` (record) | 모든 REST 응답의 통일 포맷. traceId 자동 삽입 |
| `com.michelet.common.exception` | `ErrorCode` (interface) | 도메인별 에러 코드 enum이 구현 |
| `com.michelet.common.exception` | `BusinessException` | 비즈니스 규칙 위반 예외 (RuntimeException) |
| `com.michelet.common.exception` | `GlobalExceptionHandler` | `@RestControllerAdvice` 전역 예외 처리기 |
| `com.michelet.common.entity` | `BaseEntity` | JPA Auditing + Soft Delete 베이스 엔티티 |
| `com.michelet.common.config` | `CommonAutoConfiguration` | common 모듈 자동 설정 진입점 |
| `com.michelet.common.config` | `JpaAuditingConfig` | `@EnableJpaAuditing` 활성화 |

---

## 기술 스택

- **JDK**: 17
- **Spring Boot**: 3.5.14
- **Build Tool**: Gradle (java-library plugin)
- **GroupId**: `com.michelet`

---

## 서비스에서 사용하기

common 은 **Spring Boot Auto-Configuration** 으로 등록되므로,
의존만 걸면 `GlobalExceptionHandler` 와 `@EnableJpaAuditing` 이 자동 적용된다.
**별도의 `scanBasePackages` 설정은 필요 없다.**

### 1. 의존성 추가

각 서비스의 `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Miche-Let:common:main-SNAPSHOT'
}
```

> `<TAG-or-COMMIT>` 는 깃 태그(`v0.0.1` 등), 브랜치(`main-SNAPSHOT`), 또는 커밋 해시.

### 2. `AuditorAware<UUID>` Bean 등록 (필수)

`BaseEntity` 의 `createdBy` / `updatedBy` 를 채우려면 각 서비스가 **현재 로그인 사용자의 UUID** 를 제공해야 한다. common 은 인증 문맥을 알 수 없으므로 이 Bean 은 서비스에서 반드시 등록.

```java
@Configuration
public class AuditorConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            // 예시: SecurityContext / MDC / 요청 헤더 등에서 userId 추출
            String userId = MDC.get("userId");
            return userId != null
                    ? Optional.of(UUID.fromString(userId))
                    : Optional.empty();
        };
    }
}
```

> ⚠️ 이 Bean 이 없거나 `Optional.empty()` 를 반환하면
> `BaseEntity.createdBy` 의 `NOT NULL` 제약에 걸려 INSERT 가 실패한다.

---

## 사용 예시

**성공 응답**
```java
@GetMapping("/{id}")
public ApiResponse<UserResponse> getUser(@PathVariable UUID id) {
    return ApiResponse.success(userService.get(id));
}
```

**비즈니스 예외**
```java
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_001", "유저를 찾을 수 없습니다.", 404);
    // ... getCode(), getMessage(), getHttpStatus() 구현
}

throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
```

**엔티티 상속**
```java
@Entity
public class Waiting extends BaseEntity {
    @Id @GeneratedValue private UUID id;
    // createdAt, createdBy, updatedAt, updatedBy, deletedAt, deletedBy 자동 제공
}
```


## 로컬 빌드

```bash
./gradlew clean build
./gradlew publishToMavenLocal   # 로컬에서 다른 서비스가 바로 쓰고 싶을 때
```

---

## common 패키지 구조

```text
common/
├── build.gradle
├── settings.gradle
└── src/
    └── main/
        ├── java/com/michelet/common/
        │   ├── config/
        │   │   ├── CommonAutoConfiguration.java
        │   │   └── JpaAuditingConfig.java
        │   ├── entity/
        │   │   └── BaseEntity.java
        │   ├── exception/
        │   │   ├── BusinessException.java
        │   │   ├── ErrorCode.java
        │   │   └── GlobalExceptionHandler.java
        │   └── response/
        │       ├── ApiResponse.java
        │       └── SuccessCode.java
        └── resources/
            └── META-INF/spring/
                └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```
