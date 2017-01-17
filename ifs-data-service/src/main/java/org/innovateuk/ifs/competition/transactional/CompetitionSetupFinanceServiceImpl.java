package org.innovateuk.ifs.competition.transactional;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.matchAll;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;


/**
 * Service implementation to deal with the finance part of competition setup.
 */
@Service
public class CompetitionSetupFinanceServiceImpl extends BaseTransactionalService implements CompetitionSetupFinanceService {


    @Autowired
    private FormInputRepository formInputRepository;

    @Override
    public ServiceResult<Void> save(CompetitionSetupFinanceResource compSetupFinanceRes) {
        Long compId = compSetupFinanceRes.getCompetitionId();
        ServiceResult<Void> save = saveCountAndTurnover(compSetupFinanceRes).
                andOnSuccess(() -> saveFinance(compSetupFinanceRes)).
                        andOnSuccess(competition(compId)).
                        andOnSuccessReturnVoid(competition -> competition.setFullApplicationFinance(compSetupFinanceRes.isFullApplicationFinance()));
        return  save;

    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(Long competitionId) {
        ServiceResult<Boolean> isIncludeGrowthTableResult = isIncludeGrowthTable(competitionId);
        ServiceResult<CompetitionSetupFinanceResource> csfrResult = find(isIncludeGrowthTableResult, getCompetition(competitionId)).
                andOnSuccess((isIncludeGrowthTable, competition) -> {
                    CompetitionSetupFinanceResource csfr = new CompetitionSetupFinanceResource();
                    csfr.setIncludeGrowthTable(isIncludeGrowthTable);
                    csfr.setFullApplicationFinance(competition.isFullApplicationFinance());
                    csfr.setCompetitionId(competitionId);
                    return serviceSuccess(csfr);
                });
        return csfrResult;
    }


    private ServiceResult<Void> saveCountAndTurnover(CompetitionSetupFinanceResource compSetupFinanceRes){
        Long compId = compSetupFinanceRes.getCompetitionId();
        ServiceResult<Void> saveCountAndTurnover = find(countInput(compId), turnoverInput(compId))
                .andOnSuccess((count, turnover) -> {
                    boolean isActive = !compSetupFinanceRes.isIncludeGrowthTable();
                    count.setActive(isActive);
                    turnover.setActive(isActive);
                    return ServiceResult.serviceSuccess();
                });
        return saveCountAndTurnover;
    }

    private ServiceResult<Void> saveFinance(CompetitionSetupFinanceResource compSetupFinanceRes){
        Long compId = compSetupFinanceRes.getCompetitionId();
        ServiceResult<Void> saveFinance = find(financeCount(compId), financeOverviewRow(compId), financeYearEnd(compId))
                .andOnSuccess((count, overviewRows, yearEnd) -> {
                    boolean isActive = compSetupFinanceRes.isIncludeGrowthTable();
                    count.setActive(isActive);
                    yearEnd.setActive(isActive);
                    overviewRows.forEach(row -> row.setActive(isActive));
                    return ServiceResult.serviceSuccess();
                });
        return saveFinance;
    }

    private ServiceResult<Boolean> isIncludeGrowthTable(Long competitionId) {
        ServiceResult<Boolean> isIncludeGrowthTable = find(isIncludeGrowthTableByCountAndTurnover(competitionId), isIncludeGrowthTableByFinance(competitionId)).
                andOnSuccess((byCountAndTurnover, byFinance) -> {
                    boolean isConsistent = byCountAndTurnover ^ byFinance;
                    if (isConsistent) {
                        return serviceSuccess(byCountAndTurnover);
                    } else {
                        return serviceFailure(GENERAL_UNEXPECTED_ERROR);
                    }
                });
        return isIncludeGrowthTable;
    }

    private ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnover(Long competitionId) {
        ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnOver = find(countInput(competitionId), turnoverInput(competitionId)).
                andOnSuccess((count, turnover) -> {
                    boolean isConsistent = count.getActive() ^ turnover.getActive();
                    if (isConsistent) {
                        return serviceSuccess(!count.getActive());
                    } else {
                        return serviceFailure(GENERAL_UNEXPECTED_ERROR);
                    }
                });
        return isIncludeGrowthTableByCountAndTurnOver;
    }

    private ServiceResult<Boolean> isIncludeGrowthTableByFinance(Long competitionId) {
        ServiceResult<Boolean> isIncludeGrowthTableByFinance = find(financeCount(competitionId), financeOverviewRow(competitionId), financeYearEnd(competitionId)).
                andOnSuccess((count, overviewRows, yearEnd) -> {
                    boolean isConsistent = count.getActive() == yearEnd.getActive() && matchAll(overviewRows, row -> row.getActive() == count.getActive());
                    if (isConsistent) {
                        return serviceSuccess(!count.getActive());
                    } else {
                        return serviceFailure(GENERAL_UNEXPECTED_ERROR);
                    }
                });
        return isIncludeGrowthTableByFinance;
    }

    private ServiceResult<FormInput> countInput(Long competitionId) {
        return getOnlyForCompetition(competitionId, STAFF_COUNT);
    }

    private ServiceResult<FormInput> turnoverInput(Long competitionId) {
        return getOnlyForCompetition(competitionId, STAFF_TURNOVER);
    }

    private ServiceResult<FormInput> getOnlyForCompetition(Long competitionId, FormInputType formInputType) {
        List<FormInput> all = formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(formInputType));
        return getOnlyElementOrFail(all);
    }

    private ServiceResult<FormInput> financeCount(Long competitionId) {
        return getOnlyForCompetition(competitionId, FINANCIAL_STAFF_COUNT);
    }

    private ServiceResult<FormInput> financeYearEnd(Long competitionId) {
        return getOnlyForCompetition(competitionId, FINANCIAL_YEAR_END);
    }

    private ServiceResult<List<FormInput>> financeOverviewRow(Long competitionId) {
        return serviceSuccess(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW)));
    }


}
