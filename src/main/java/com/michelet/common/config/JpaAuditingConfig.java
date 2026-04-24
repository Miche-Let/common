package com.michelet.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정.
 * 각 서비스에서 별도 @EnableJpaAuditing 을 달 필요 없이, common-module 의존성만으로 동작한다.
 *
 * 각 서비스는 AuditorAware<UUID> Bean 을 반드시 등록해야 한다
 * (common 은 누가 로그인 중인지 알 수 없기 때문).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
