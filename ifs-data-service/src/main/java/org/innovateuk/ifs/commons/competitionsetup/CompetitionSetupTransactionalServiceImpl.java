package org.innovateuk.ifs.commons.competitionsetup;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.form.resource.FormInputType.FINANCIAL_OVERVIEW_ROW;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;

/**
 * Common processing for accessing Competition Setup details
 */
@Transactional
@Component
public class CompetitionSetupTransactionalServiceImpl extends BaseTransactionalService implements CompetitionSetupTransactionalService {

    @Autowired
    private FormInputRepository formInputRepository;

    @Override
    public ServiceResult<Boolean> isIncludeGrowthTable(Long compId) {
        ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnover = find(countInput(compId), turnoverInput(compId)).andOnSuccess(this::isIncludeGrowthTableByCountAndTurnover);
        ServiceResult<Boolean> isIncludeGrowthTableByFinance = find(financeYearEnd(compId), financeOverviewRow(compId), financeCount(compId)).andOnSuccess(this::isIncludeGrowthTableByFinance);
        return find(isIncludeGrowthTableByCountAndTurnover, isIncludeGrowthTableByFinance).andOnSuccess(this::isIncludeGrowthTableByCountTurnoverAndFinance);
    }

    @Override
    public ServiceResult<FormInput> countInput(Long competitionId) {
        return getOnlyForCompetition(competitionId, STAFF_COUNT);
    }

    @Override
    public ServiceResult<FormInput> turnoverInput(Long competitionId) {
        return getOnlyForCompetition(competitionId, STAFF_TURNOVER);
    }

    @Override
    public ServiceResult<FormInput> financeCount(Long competitionId) {
        return getOnlyForCompetition(competitionId, FINANCIAL_STAFF_COUNT);
    }

    @Override
    public ServiceResult<FormInput> financeYearEnd(Long competitionId) {
        return getOnlyForCompetition(competitionId, FINANCIAL_YEAR_END);
    }

    @Override
    public ServiceResult<List<FormInput>> financeOverviewRow(Long competitionId) {
        return serviceSuccess(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW)));
    }

    private ServiceResult<FormInput> getOnlyForCompetition(Long competitionId, FormInputType formInputType) {
        List<FormInput> all = formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(formInputType));
        return getOnlyElementOrFail(all);
    }


    private ServiceResult<Boolean> isIncludeGrowthTableByCountTurnoverAndFinance(boolean byCountAndTurnover, boolean byFinance) {
        boolean isConsistent = byCountAndTurnover == byFinance;
        if (isConsistent) {
            return serviceSuccess(byCountAndTurnover);
        } else {
            return serviceFailure(new Error("include.growth.table.count.turnover.finance.input.active.not.consistent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    private ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnover(FormInput count, FormInput turnover) {
        boolean isConsistent = count.getActive() == turnover.getActive();
        if (isConsistent) {
            return serviceSuccess(!count.getActive());
        } else {
            return serviceFailure(new Error("include.growth.table.count.turnover.input.active.not.consistent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private ServiceResult<Boolean> isIncludeGrowthTableByFinance(FormInput yearEnd, List<FormInput> overviewRows, FormInput count) {
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
}
