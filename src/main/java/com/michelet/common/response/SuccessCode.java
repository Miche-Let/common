package com.michelet.common.response;

/**
 * 성공 응답에 부가적인 의미 코드를 담고 싶을 때 사용하는 인터페이스.
 * 각 도메인 서비스에서 enum 으로 구현한다.
 *
 * <pre>
 * public enum OrderSuccessCode implements SuccessCode {
 *     ORDER_CREATED("ORDER_SUCCESS_001", "주문이 생성되었습니다."),
 *     ORDER_CANCELED("ORDER_SUCCESS_002", "주문이 취소되었습니다.");
 *
 *     private final String code;
 *     private final String message;
 *
 *     OrderSuccessCode(String code, String message) {
 *         this.code = code;
 *         this.message = message;
 *     }
 *
 *     &#64;Override public String getCode()    { return code; }
 *     &#64;Override public String getMessage() { return message; }
 * }
 * </pre>
 */
public interface SuccessCode {
    String getCode();
    String getMessage();
}
