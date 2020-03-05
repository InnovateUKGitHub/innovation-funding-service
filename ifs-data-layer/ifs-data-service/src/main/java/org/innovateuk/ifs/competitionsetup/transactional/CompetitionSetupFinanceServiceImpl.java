package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service implementation to deal with the finance part of competition setup.
 */
@Service
public class CompetitionSetupFinanceServiceImpl extends BaseTransactionalService implements
        CompetitionSetupFinanceService {

    @Override
    @Transactional
    public ServiceResult<Void> save(CompetitionSetupFinanceResource compSetupFinanceRes) {
        return getCompetition(compSetupFinanceRes.getCompetitionId()).andOnSuccess(competition -> {
            competition.setApplicationFinanceType(compSetupFinanceRes.getApplicationFinanceType());
            competition.setIncludeJesForm(compSetupFinanceRes.getIncludeJesForm());
            competition.setIncludeProjectGrowthTable(compSetupFinanceRes.getIncludeGrowthTable());
            competition.setIncludeYourOrganisationSection(compSetupFinanceRes.getIncludeYourOrganisationSection());
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(this::buildCompetitionSetupFinanceResource);
    }

    private CompetitionSetupFinanceResource buildCompetitionSetupFinanceResource(Competition competition) {
        CompetitionSetupFinanceResource competitionSetupFinanceResource = new CompetitionSetupFinanceResource();
        competitionSetupFinanceResource.setCompetitionId(competition.getId());
        competitionSetupFinanceResource.setApplicationFinanceType(competition.getApplicationFinanceType());
        competitionSetupFinanceResource.setIncludeJesForm(competition.getIncludeJesForm());
        competitionSetupFinanceResource.setIncludeGrowthTable(competition.getIncludeProjectGrowthTable());
        competitionSetupFinanceResource.setIncludeYourOrganisationSection(competition.getIncludeYourOrganisationSection());
        return competitionSetupFinanceResource;
    }
}
