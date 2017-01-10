package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;

/**
 * Rest service interface for dealing with the finance part of the competition setup.
 */
public interface CompetitionSetupFinanceRestService {

    RestResult<Void> save(CompetitionSetupFinanceResource competitionSetupFinanceResource);

}
