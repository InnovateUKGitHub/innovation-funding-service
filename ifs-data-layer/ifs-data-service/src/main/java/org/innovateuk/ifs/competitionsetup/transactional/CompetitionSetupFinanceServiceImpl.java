package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return isNoFinances(competition) ? serviceSuccess() :
                    saveCountAndTurnover(compSetupFinanceRes).andOnSuccess(() -> saveFinance(compSetupFinanceRes));
        });
    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(long competitionId) {
        return getCompetition(competitionId).andOnSuccessReturn(competition -> {
            Boolean includeGrowthTable = isNoFinances(competition) ? false : competitionSetupTransactionalService
                    .isIncludeGrowthTable(competitionId).getSuccess();

            return buildCompetitionSetupFinanceResource(competition, includeGrowthTable);
        });
    }


    private ServiceResult<Void> saveCountAndTurnover(CompetitionSetupFinanceResource compSetupFinanceRes) {
        Long compId = compSetupFinanceRes.getCompetitionId();

        return find(competitionSetupTransactionalService.countInput(compId), competitionSetupTransactionalService
                .turnoverInput(compId))
                .andOnSuccess((count, turnover) -> {
                    boolean isActive = compSetupFinanceRes.getIncludeGrowthTable() == null
                            || !compSetupFinanceRes.getIncludeGrowthTable();
                    count.setActive(isActive);
                    turnover.setActive(isActive);
                    return serviceSuccess();
                });
    }

    private ServiceResult<Void> saveFinance(CompetitionSetupFinanceResource compSetupFinanceRes) {
        Long compId = compSetupFinanceRes.getCompetitionId();
        return find(competitionSetupTransactionalService.financeCount(compId), competitionSetupTransactionalService
                .financeOverviewRow(compId), competitionSetupTransactionalService.financeYearEnd(compId))
                .andOnSuccess((count, overviewRows, yearEnd) -> {
                    boolean isActive = compSetupFinanceRes.getIncludeGrowthTable() != null
                            && compSetupFinanceRes.getIncludeGrowthTable();
                    count.setActive(isActive);
                    yearEnd.setActive(isActive);
                    overviewRows.forEach(row -> row.setActive(isActive));
                    return serviceSuccess();
                });
    }

    private boolean isNoFinances(Competition competition) {
        return competition.getApplicationFinanceType() == null || competition.getApplicationFinanceType() == NO_FINANCES;
    }

    private CompetitionSetupFinanceResource buildCompetitionSetupFinanceResource(Competition competition,
                                                                                 Boolean includeGrowthTable) {
        CompetitionSetupFinanceResource competitionSetupFinanceResource = new CompetitionSetupFinanceResource();
        competitionSetupFinanceResource.setCompetitionId(competition.getId());
        competitionSetupFinanceResource.setApplicationFinanceType(competition.getApplicationFinanceType());
        competitionSetupFinanceResource.setIncludeGrowthTable(includeGrowthTable);
        return competitionSetupFinanceResource;
    }
}
