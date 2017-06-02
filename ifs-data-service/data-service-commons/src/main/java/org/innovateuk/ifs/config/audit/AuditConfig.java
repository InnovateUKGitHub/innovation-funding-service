package org.innovateuk.ifs.config.audit;

import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration to enable Spring Data JPA Auditing. Has an auditorProdivder to give the provide the logged in
 * {@link org.innovateuk.ifs.user.domain.User} as the auditor.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Autowired
    private UserMapper userMapper;

    @Bean
    public AuditorAware<User> auditorProvider() {
        return () -> {
            UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            else {
                return userMapper.mapToDomain(authentication.getDetails());
            }
        };
    }
}

