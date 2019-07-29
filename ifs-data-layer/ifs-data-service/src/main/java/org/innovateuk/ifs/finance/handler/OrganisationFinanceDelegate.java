package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationFinanceDelegate {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandler;

    @Autowired
    private JesFinanceHandler organisationJESFinance;

    @Autowired
    private FinanceUtil financeUtil;

    public OrganisationTypeFinanceHandler getOrganisationFinanceHandler(Long competitionId, Long organisationType) {
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();
        if (financeUtil.isUsingJesFinances(competition, organisationType)) {
            return organisationJESFinance;
        } else {
            return organisationFinanceDefaultHandler;
        }
    }
}
