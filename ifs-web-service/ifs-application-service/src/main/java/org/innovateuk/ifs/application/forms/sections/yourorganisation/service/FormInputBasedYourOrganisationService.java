package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.ORGANISATION_TURNOVER;

/**
 * TODO DW - document this class
 */
@Service
public class FormInputBasedYourOrganisationService implements YourOrganisationService {

    private QuestionRestService questionRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private ApplicationRestService applicationRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;

    public FormInputBasedYourOrganisationService(QuestionRestService questionRestService,
                                                 FormInputResponseRestService formInputResponseRestService,
                                                 ApplicationRestService applicationRestService,
                                                 ApplicationFinanceRestService applicationFinanceRestService) {

        this.questionRestService = questionRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.applicationRestService = applicationRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    @Override
    public ServiceResult<Long> getTurnover(long applicationId, long competitionId) {
        return getLongValueForFormInputType(applicationId, competitionId, ORGANISATION_TURNOVER);
    }

    @Override
    public ServiceResult<Long> getHeadCount(long applicationId, long competitionId) {
        return getLongValueForFormInputType(applicationId, competitionId, FormInputType.STAFF_COUNT);
    }

    @Override
    public ServiceResult<Boolean> getStateAidEligibility(long applicationId) {

        CompetitionResource competition =
                applicationRestService.getCompetitionByApplicationId(applicationId).getSuccess();

        return serviceSuccess(TRUE.equals(competition.getStateAid()));

    }

    @Override
    public ServiceResult<OrganisationSize> getOrganisationSize(long applicationId, long organisationId) {
        return applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).
                andOnSuccessReturn(ApplicationFinanceResource::getOrganisationSize).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateTurnover(long applicationId, long competitionId, Long value) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> updateHeadCount(long applicationId, long competitionId, Long value) {
        return serviceSuccess();
    }

    private ServiceResult<Long> getLongValueForFormInputType(long applicationId, long competitionId, FormInputType formInputType) {
        return questionRestService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputResponseRestService.getByApplicationIdAndQuestionId(applicationId, question.getId())).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses).
                toServiceResult();
    }

    private Long getLongValueFromFormInputResponses(List<FormInputResponseResource> formInputResponses) {

        if (formInputResponses.isEmpty()) {
            return null;
        } else {
            String value = formInputResponses.get(0).getValue();

            if (value == null) {
                return null;
            }

            return Long.valueOf(value);
        }
    }
}
