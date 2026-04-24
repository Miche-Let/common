package com.michelet.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.LocalDateTime;

/**
 * 모든 REST API 응답의 통일된 포맷.
 * 성공/실패 구분, 분산 추적을 위한 traceId 를 포함한다.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String code,
        String message,
        LocalDateTime timestamp,
        String traceId
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, LocalDateTime.now(), currentTraceId());
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null, null, LocalDateTime.now(), currentTraceId());
    }

    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(true, data, code.getCode(), code.getMessage(),
                LocalDateTime.now(), currentTraceId());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, code, message, LocalDateTime.now(), currentTraceId());
    }

    /**
     * Micrometer Tracing 이 MDC 에 넣어주는 traceId 를 꺼낸다.
     * 로그와 응답의 traceId 를 일치시키는 것이 목적.
     */
    private static String currentTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : MDC.get("X-B3-TraceId");
    }
}
