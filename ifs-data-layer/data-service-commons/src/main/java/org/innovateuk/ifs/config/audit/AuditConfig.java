package org.innovateuk.ifs.config.audit;

import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuration to enable Spring Data JPA Auditing. Has an auditorProvider to give the provide the logged in
 * {@link org.innovateuk.ifs.user.domain.User} as the auditor.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<User> auditorProvider(UserMapper userMapper) {
        return () -> {
            UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            else {
                return Optional.of(userMapper.mapToDomain(authentication.getDetails()));
            }
        };
    }
}

