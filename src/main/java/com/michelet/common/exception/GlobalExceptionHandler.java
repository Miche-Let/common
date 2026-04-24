package com.michelet.common.exception;

import com.michelet.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 전역 예외 처리기.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 (404, 403, 409 등)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("[BusinessException] code={}, message={}", e.getErrorCode(), e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    // @Valid 유효성 검증 실패 (요청 바디)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("유효성 검증 실패");
        log.warn("[Validation] {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_001", message));
    }

    // Enum / UUID 등 타입 변환 실패 (@RequestParam, @PathVariable)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = e.getName() + " 파라미터 형식이 올바르지 않습니다.";
        log.warn("[TypeMismatch] param={}, requiredType={}",
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_002", message));
    }

    // 헤더 누락, 요청 바디 파싱 실패 (JSON 구조 오류 등)
    @ExceptionHandler({MissingRequestHeaderException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
        log.warn("[BadRequest] type={}", e.getClass().getSimpleName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_003", "요청 형식이 올바르지 않습니다."));
    }

    // 그 외 예상치 못한 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Unexpected Error]", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_001", "서버 오류가 발생했습니다."));
    }
}
