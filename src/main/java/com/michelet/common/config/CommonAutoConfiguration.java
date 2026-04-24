package com.michelet.common.config;

import com.michelet.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * common-module 자동 설정 진입점.
 *
 */
@AutoConfiguration
@Import({
        JpaAuditingConfig.class,
        GlobalExceptionHandler.class
})
public class CommonAutoConfiguration {
}

