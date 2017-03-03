package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;


/**
 * Service interface to deal with the finance part of competition setup.
 */
public interface CompetitionSetupFinanceService {

    ServiceResult<Void> updateFinance(CompetitionSetupFinanceResource question);

	CompetitionSetupFinanceResource getByCompetitionId(Long competitionId);

}
