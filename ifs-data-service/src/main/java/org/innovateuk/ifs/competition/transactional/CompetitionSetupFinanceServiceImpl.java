package org.innovateuk.ifs.competition.transactional;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.STAFF_COUNT;
import static org.innovateuk.ifs.form.resource.FormInputType.STAFF_TURNOVER;
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
    public ServiceResult<Void> save(CompetitionSetupFinanceResource csfr) {
        Long compId = csfr.getCompetitionId();

        ServiceResult<Void> isIncludeGrowthTableResult = find(countInput(compId), turnoverInput(compId))
                .andOnSuccess((count, turnover) -> {
                    boolean isActive = !csfr.isIncludeGrowthTable();
                    count.setActive(isActive);
                    turnover.setActive(isActive);
                    return ServiceResult.serviceSuccess();
                });
        isIncludeGrowthTableResult.
                andOnSuccess(competition(compId)).
                andOnSuccessReturnVoid(competition -> {
                    competition.setFullApplicationFinance(csfr.isFullApplicationFinance());
                });
        return isIncludeGrowthTableResult;
    }

    @Override
    public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(Long competitionId) {
        ServiceResult<Boolean> isIncludeGrowthTableResult = find(countInput(competitionId), turnoverInput(competitionId)).
                andOnSuccess((count, turnover) -> {
                    if (count.getActive() != turnover.getActive()) {
                        return serviceFailure(GENERAL_UNEXPECTED_ERROR);
                    } else {
                        return serviceSuccess(!count.getActive());
                    }
                });
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

}
