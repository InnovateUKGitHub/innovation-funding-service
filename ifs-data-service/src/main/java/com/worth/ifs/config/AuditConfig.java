package com.worth.ifs.config;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration to enable Spring Data JPA Auditing. Has an auditorProdivder to give the provide the logged in
 * {@link com.worth.ifs.user.domain.User} as the auditor.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Autowired
    private UserMapper userMapper;

    @Bean
    public AuditorAware<UserResource> auditorProvider() {
        return new AuditorAware() {
            @Override
            public Object getCurrentAuditor() {
                UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                    return null;
                }
                else {
                    return userMapper.mapToDomain(authentication.getDetails());
                }
            }
        };
    }
}

