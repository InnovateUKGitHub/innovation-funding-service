package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.ORGANISATION_TURNOVER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;

/**
 * TODO DW - document this class
 */
@Service
public class FormInputBasedYourOrganisationService implements YourOrganisationService {

    private QuestionRestService questionRestService;
    private FormInputRestService formInputRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private ApplicationRestService applicationRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;

    public FormInputBasedYourOrganisationService(QuestionRestService questionRestService,
                                                 FormInputRestService formInputRestService,
                                                 FormInputResponseRestService formInputResponseRestService,
                                                 ApplicationRestService applicationRestService,
                                                 ApplicationFinanceRestService applicationFinanceRestService) {

        this.questionRestService = questionRestService;
        this.formInputRestService = formInputRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.applicationRestService = applicationRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    @Override
    public ServiceResult<Long> getTurnover(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, ORGANISATION_TURNOVER);
    }

    @Override
    public ServiceResult<Long> getHeadCount(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.STAFF_COUNT);
    }

    @Override
    public ServiceResult<Boolean> getStateAidAgreed(long applicationId) {
        return applicationRestService.getApplicationById(applicationId).
                toServiceResult().
                andOnSuccessReturn(ApplicationResource::getStateAidAgreed);
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
    public ServiceResult<Void> updateTurnover(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.ORGANISATION_TURNOVER);
    }

    @Override
    public ServiceResult<Void> updateHeadCount(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.STAFF_COUNT);
    }

    @Override
    public ServiceResult<Void> updateStateAidAgreed(long applicationId, boolean stateAidAgreed) {
        return applicationRestService.getApplicationById(applicationId).
                andOnSuccess(application -> {
                    application.setStateAidAgreed(stateAidAgreed);
                    return applicationRestService.saveApplication(application);
                }).
                toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateOrganisationSize(long applicationId, long organisationId, OrganisationSize organisationSize) {
        return applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).
                andOnSuccess(finance -> {
                    finance.setOrganisationSize(organisationSize);
                    return applicationFinanceRestService.update(finance.getId(), finance);
                }).
                toServiceResult().
                andOnSuccessReturnVoid();
    }

    private ServiceResult<Long> getLongValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses).
                toServiceResult();
    }

    private ServiceResult<Void> updateLongValueForFormInput(long applicationId, long competitionId, long userId, Long value, FormInputType formInputType) {

        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputRestService.getByQuestionId(question.getId())).
                andOnSuccessReturn(formInputs -> simpleFindFirstMandatory(formInputs, fi -> formInputType.equals(fi.getType()))).
                andOnSuccessReturn(formInput -> formInputResponseRestService.saveQuestionResponse(
                        userId,
                        applicationId,
                        formInput.getId(),
                        value != null ? value.toString() : null,
                        false)).
                toServiceResult().
                andOnSuccessReturnVoid();
    }

    private RestResult<Optional<FormInputResponseResource>> getFormInputResponseForOrganisation(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputResponseRestService.getByApplicationIdQuestionIdOrganisationIdAndFormInputType(applicationId, question.getId(), organisationId, formInputType)).
                toOptionalIfNotFound();
    }

    private RestResult<QuestionResource> getQuestionByCompetitionIdAndFormInputType(long competitionId, FormInputType formInputType) {
        return questionRestService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType);
    }

    private Long getLongValueFromFormInputResponses(Optional<FormInputResponseResource> formInputResponse) {

        return formInputResponse.
                map(response -> NumberUtils.isDigits(response.getValue()) ? Long.valueOf(response.getValue()) : null).
                orElse(null);
    }
}
