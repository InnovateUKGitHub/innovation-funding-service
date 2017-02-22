package org.innovateuk.ifs.competition.transactional;


import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
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
        return save;

    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(Long compId) {
        ServiceResult<Boolean> isIncludeGrowthTableResult = isIncludeGrowthTable(compId);

        ServiceResult<Long> turnoverResult = getTurnover(compId);

        ServiceResult<Long> headcountResult = getStaffCount(compId);

        ServiceResult<CompetitionSetupFinanceResource> compSetupFinanceResResult = find(isIncludeGrowthTableResult, getCompetition(compId)).
                andOnSuccess((isIncludeGrowthTable, competition) -> {
                    CompetitionSetupFinanceResource compSetupFinanceRes = new CompetitionSetupFinanceResource();
                    compSetupFinanceRes.setIncludeGrowthTable(isIncludeGrowthTable);
                    compSetupFinanceRes.setFullApplicationFinance(competition.isFullApplicationFinance());
                    compSetupFinanceRes.setCompetitionId(compId);
                    if (turnoverResult.isSuccess()) {
                        compSetupFinanceRes.setTurnover(turnoverResult.getSuccessObject());
                    }
                    if (headcountResult.isSuccess()) {
                        compSetupFinanceRes.setHeadcount(headcountResult.getSuccessObject());
                    }
                    return serviceSuccess(compSetupFinanceRes);
                });
        return compSetupFinanceResResult;
    }

    private ServiceResult<Void> saveCountAndTurnover(CompetitionSetupFinanceResource compSetupFinanceRes) {
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

    private ServiceResult<Void> saveFinance(CompetitionSetupFinanceResource compSetupFinanceRes) {
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

    private ServiceResult<Boolean> isIncludeGrowthTable(Long compId) {
        ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnover = find(countInput(compId), turnoverInput(compId)).andOnSuccess(this::isIncludeGrowthTableByCountAndTurnover);
        ServiceResult<Boolean> isIncludeGrowthTableByFinance = find(financeYearEnd(compId), financeOverviewRow(compId), financeCount(compId)).andOnSuccess(this::isIncludeGrowthTableByFinance);
        ServiceResult<Boolean> isIncludeGrowthTable = find(isIncludeGrowthTableByCountAndTurnover, isIncludeGrowthTableByFinance).andOnSuccess(this::isIncludeGrowthTableByCountTurnoverAndFinance);
        return isIncludeGrowthTable;
    }

    ServiceResult<Boolean> isIncludeGrowthTableByCountTurnoverAndFinance(boolean byCountAndTurnover, boolean byFinance) {
        boolean isConsistent = byCountAndTurnover == byFinance;
        if (isConsistent) {
            return serviceSuccess(byCountAndTurnover);
        } else {
            return serviceFailure(new Error("include.growth.table.count.turnover.finance.input.active.not.consistent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnover(FormInput count, FormInput turnover) {
        boolean isConsistent = count.getActive() == turnover.getActive();
        if (isConsistent) {
            return serviceSuccess(!count.getActive());
        } else {
            return serviceFailure(new Error("include.growth.table.count.turnover.input.active.not.consistent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    ServiceResult<Boolean> isIncludeGrowthTableByFinance(FormInput yearEnd, List<FormInput> overviewRows, FormInput count) {
        // Check the active boolean is the same across all of the fields
        List<Boolean> overviewRowsActive = simpleMap(overviewRows, FormInput::getActive);
        boolean isConsistent =
                (count.getActive() && yearEnd.getActive() && !overviewRowsActive.contains(false))
                        || (!count.getActive() && !yearEnd.getActive() && !overviewRowsActive.contains(true));
        if (isConsistent) {
            return serviceSuccess(count.getActive());
        } else {
            return serviceFailure(new Error("include.growth.table.finance.input.active.not.consistent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
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

    private ServiceResult<Long> getStaffCount(Long competitionId) {
        ServiceResult<FormInput> countInputResult = countInput(competitionId);
        if (countInputResult.isSuccess() && countInputResult.getSuccessObject().getResponses() != null && !countInputResult.getSuccessObject().getResponses().isEmpty()) {
            List<FormInputResponse> inputs = countInputResult.getSuccessObject().getResponses();
            return ServiceResult.serviceSuccess(Long.parseLong(inputs.get(0).getValue()));
        } else {
            countInputResult = financeCount(competitionId);
            if (countInputResult.isSuccess()) {
                List<FormInputResponse> inputs = countInputResult.getSuccessObject().getResponses();
                if (inputs != null && !inputs.isEmpty()) {
                    return ServiceResult.serviceSuccess(Long.parseLong(inputs.get(0).getValue()));
                }
            }
        }
        return serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND);
    }

    private ServiceResult<Long> getTurnover(Long competitionId) {
        ServiceResult<FormInput> turnoverInputResult = turnoverInput(competitionId);
        if (turnoverInputResult.isSuccess() && turnoverInputResult.getSuccessObject().getResponses() != null && !turnoverInputResult.getSuccessObject().getResponses().isEmpty()) {
            List<FormInputResponse> inputs = turnoverInputResult.getSuccessObject().getResponses();
            return ServiceResult.serviceSuccess(Long.parseLong(inputs.get(0).getValue()));
        } else {
            turnoverInputResult = financeYearEnd(competitionId);
            if (turnoverInputResult.isSuccess()) {
                List<FormInputResponse> inputs = turnoverInputResult.getSuccessObject().getResponses();
                if (inputs != null && !inputs.isEmpty()) {
                    return ServiceResult.serviceSuccess(Long.parseLong(inputs.get(0).getValue()));
                }
            }
        }
        return serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND);
    }
}
