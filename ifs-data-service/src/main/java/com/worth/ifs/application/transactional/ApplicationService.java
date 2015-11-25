package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;

/**
 *
 */
public interface ApplicationService {

    Application createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, final Long competitionId, final Long userId);
}
