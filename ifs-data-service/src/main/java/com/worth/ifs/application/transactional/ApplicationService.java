package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.security.NotSecured;

/**
 *
 */
public interface ApplicationService {

    @NotSecured(reason="TODO")
    Application createApplicationByApplicationNameForUserTokenAndCompetitionId(String applicationName, final Long competitionId, final Long userId);
}
