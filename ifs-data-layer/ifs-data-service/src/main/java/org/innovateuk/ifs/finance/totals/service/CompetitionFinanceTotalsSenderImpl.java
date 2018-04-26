package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service sends cost totals for all submitted {@link Application}s within the {@link Competition}.
 */
@Service
public class CompetitionFinanceTotalsSenderImpl implements CompetitionFinanceTotalsSender {
    private static final Logger LOG = LoggerFactory.getLogger(CompetitionFinanceTotalsSender.class);

    @Autowired
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public ServiceResult<Void> sendFinanceTotalsForCompetition(Long competitionId) {
        LOG.debug("Initiating sendFinanceTotalsForCompetition for competitionId: {}", competitionId);

        List<Application> applications = applicationService.getApplicationsByCompetitionIdAndState(
                competitionId,
                ApplicationState.submittedAndFinishedStates).getSuccess();

        applications.forEach(app -> applicationFinanceTotalsSender.sendFinanceTotalsForApplication(app.getId()));

        return ServiceResult.serviceSuccess();
    }
}
