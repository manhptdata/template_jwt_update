package com.template.manhpt.config;


import com.template.manhpt.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

	@Bean
	public AuditorAware<Integer> auditorProvider() {
		return () -> {

			Integer currentUserId = SecurityUtil.getCurrentUserId();
			return Optional.ofNullable(currentUserId);
		};
	}
}