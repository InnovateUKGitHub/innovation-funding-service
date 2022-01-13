package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.springframework.stereotype.Service;


/**
 * Rest service implementation for dealing with the finance part of the competition setup.
 */
@Service
public class CompetitionSetupFinanceRestServiceImpl extends BaseRestService implements CompetitionSetupFinanceRestService {

    private String competitionsSetupFinanceRestURL = "/competition-setup-finance";


    @Override
    public RestResult<Void> save(CompetitionSetupFinanceResource competitionSetupFinanceResource) {
        return putWithRestResult(competitionsSetupFinanceRestURL + "/" + competitionSetupFinanceResource.getCompetitionId(),
                competitionSetupFinanceResource,
                Void.class);
    }


    @Override
    public RestResult<CompetitionSetupFinanceResource> getByCompetitionId(Long competitionId) {
        return getWithRestResult(competitionsSetupFinanceRestURL + "/" + competitionId,
                CompetitionSetupFinanceResource.class);
    }


}
