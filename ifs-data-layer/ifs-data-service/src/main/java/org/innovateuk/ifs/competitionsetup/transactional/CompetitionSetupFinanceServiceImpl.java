package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service implementation to deal with the finance part of competition setup.
 */
@Service
public class CompetitionSetupFinanceServiceImpl extends BaseTransactionalService implements
        CompetitionSetupFinanceService {

    @Autowired
    private CompetitionSetupTransactionalService competitionSetupTransactionalService;

    @Override
    @Transactional
    public ServiceResult<Void> save(CompetitionSetupFinanceResource compSetupFinanceRes) {
        return getCompetition(compSetupFinanceRes.getCompetitionId()).andOnSuccess(competition -> {
            competition.setApplicationFinanceType(compSetupFinanceRes.getApplicationFinanceType());
            competition.setIncludeJesForm(compSetupFinanceRes.getIncludeJesForm());
            competition.setIncludeProjectGrowthTable(compSetupFinanceRes.getIncludeGrowthTable());
            return isNoFinances(competition) ? serviceSuccess() : activateFormInputs(compSetupFinanceRes);
        });
    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(this::buildCompetitionSetupFinanceResource);
    }


    private ServiceResult<Void> activateFormInputs(CompetitionSetupFinanceResource compSetupFinanceRes) {
        long competitionId = compSetupFinanceRes.getCompetitionId();

        return find(
                competitionSetupTransactionalService.countInput(competitionId),
                competitionSetupTransactionalService.turnoverInput(competitionId),
                competitionSetupTransactionalService.financeCount(competitionId),
                competitionSetupTransactionalService.financeOverviewRow(competitionId),
                competitionSetupTransactionalService.financeYearEnd(competitionId))
                .andOnSuccess((countInput, turnoverInput, financeCount, financeOverviewRows, financeYearEnd) -> {
                    boolean includeGrowthTable = TRUE.equals(compSetupFinanceRes.getIncludeGrowthTable());

                    countInput.setActive(!includeGrowthTable);
                    turnoverInput.setActive(!includeGrowthTable);
                    financeCount.setActive(includeGrowthTable);
                    financeOverviewRows.forEach(row -> row.setActive(includeGrowthTable));
                    financeYearEnd.setActive(includeGrowthTable);

                    return serviceSuccess();
                });
    }

    private boolean isNoFinances(Competition competition) {
        return competition.getApplicationFinanceType() == null || competition.getApplicationFinanceType() == NO_FINANCES;
    }

    private CompetitionSetupFinanceResource buildCompetitionSetupFinanceResource(Competition competition) {
        CompetitionSetupFinanceResource competitionSetupFinanceResource = new CompetitionSetupFinanceResource();
        competitionSetupFinanceResource.setCompetitionId(competition.getId());
        competitionSetupFinanceResource.setApplicationFinanceType(competition.getApplicationFinanceType());
        competitionSetupFinanceResource.setIncludeJesForm(competition.getIncludeJesForm());
        competitionSetupFinanceResource.setIncludeGrowthTable(competition.getIncludeProjectGrowthTable());
        return competitionSetupFinanceResource;
    }
}
