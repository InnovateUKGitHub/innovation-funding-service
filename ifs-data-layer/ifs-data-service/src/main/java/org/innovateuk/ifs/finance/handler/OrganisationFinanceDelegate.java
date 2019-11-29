package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Component
public class OrganisationFinanceDelegate {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandler;

    @Autowired
    private JesFinanceHandler organisationJESFinance;

    public OrganisationTypeFinanceHandler getOrganisationFinanceHandler(Long competitionId, Long organisationType) {
        Competition competition = find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId)).getSuccess();
        if (competition.applicantShouldUseJesFinances(OrganisationTypeEnum.getFromId(organisationType))) {
            return organisationJESFinance;
        } else {
            return organisationFinanceDefaultHandler;
        }
    }
}
