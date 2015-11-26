package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Application processing work
 */
public interface ApplicationService {

    @PreAuthorize("hasAuthority('applicant')")
    Application createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, final Long competitionId, final Long userId);
}
