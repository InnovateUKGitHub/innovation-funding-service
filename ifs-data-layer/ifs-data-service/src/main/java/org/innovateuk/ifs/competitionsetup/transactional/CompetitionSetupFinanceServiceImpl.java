package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service implementation to deal with the finance part of competition setup.
 */
@Service
public class CompetitionSetupFinanceServiceImpl extends BaseTransactionalService implements CompetitionSetupFinanceService {

    @Autowired
    private CompetitionSetupTransactionalService competitionSetupTransactionalService;

    @Override
    @Transactional
    public ServiceResult<Void> save(CompetitionSetupFinanceResource compSetupFinanceRes) {
        return saveCountAndTurnover(compSetupFinanceRes).
                andOnSuccess(() -> saveFinance(compSetupFinanceRes)).
                andOnSuccess(competition(compSetupFinanceRes.getCompetitionId())).
                andOnSuccessReturnVoid(competition ->
                        competition.setApplicationFinanceType(compSetupFinanceRes.getApplicationFinanceType()));
    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(Long compId) {
        ServiceResult<Boolean> isIncludeGrowthTableResult = competitionSetupTransactionalService.isIncludeGrowthTable(compId);

        ServiceResult<CompetitionSetupFinanceResource> compSetupFinanceResResult = find(isIncludeGrowthTableResult, getCompetition(compId)).
                andOnSuccess((isIncludeGrowthTable, competition) -> {
                    CompetitionSetupFinanceResource compSetupFinanceRes = new CompetitionSetupFinanceResource();
                    compSetupFinanceRes.setIncludeGrowthTable(isIncludeGrowthTable);
                    compSetupFinanceRes.setApplicationFinanceType(competition.getApplicationFinanceType());
                    compSetupFinanceRes.setCompetitionId(compId);
                    return serviceSuccess(compSetupFinanceRes);
                });
        return compSetupFinanceResResult;
    }

    
    private ServiceResult<Void> saveCountAndTurnover(CompetitionSetupFinanceResource compSetupFinanceRes) {
        Long compId = compSetupFinanceRes.getCompetitionId();

        return find(competitionSetupTransactionalService.countInput(compId), competitionSetupTransactionalService
                .turnoverInput(compId))
                .andOnSuccess((count, turnover) -> {
                    boolean isActive = !compSetupFinanceRes.isIncludeGrowthTable();
                    count.setActive(isActive);
                    turnover.setActive(isActive);
                    return ServiceResult.serviceSuccess();
                });
    }

    private ServiceResult<Void> saveFinance(CompetitionSetupFinanceResource compSetupFinanceRes) {
        Long compId = compSetupFinanceRes.getCompetitionId();
        return find(competitionSetupTransactionalService.financeCount(compId), competitionSetupTransactionalService
                .financeOverviewRow(compId), competitionSetupTransactionalService.financeYearEnd(compId))
                .andOnSuccess((count, overviewRows, yearEnd) -> {
                    boolean isActive = compSetupFinanceRes.isIncludeGrowthTable();
                    count.setActive(isActive);
                    yearEnd.setActive(isActive);
                    overviewRows.forEach(row -> row.setActive(isActive));
                    return ServiceResult.serviceSuccess();
                });
    }

}
