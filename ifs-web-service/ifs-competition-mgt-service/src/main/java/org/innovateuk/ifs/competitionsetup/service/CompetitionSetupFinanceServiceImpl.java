package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation to deal with the finance part of competition setup.
 */
@Service
public class CompetitionSetupFinanceServiceImpl implements CompetitionSetupFinanceService {

    @Autowired
    private CompetitionSetupFinanceRestService competitionSetupFinanceRestService;


    @Override
    public ServiceResult<Void> updateFinance(CompetitionSetupFinanceResource competitionSetupFinanceResource) {
        return competitionSetupFinanceRestService.save(competitionSetupFinanceResource).toServiceResult();
    }

    @Override
    public CompetitionSetupFinanceResource getByCompetitionId(Long competitionId) {
        return competitionSetupFinanceRestService.getByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }


}
