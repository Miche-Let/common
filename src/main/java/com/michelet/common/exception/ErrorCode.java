package com.michelet.common.exception;

// 각 도메인별 에러 코드 enum이 이 인터페이스를 구현
// 예: UserErrorCode implements ErrorCode
public interface ErrorCode {
    String getCode(); // "USER_001"
    String getMessage(); // "유저를 찾을 수 없습니다."
    int getHttpStatus(); // 404
}
