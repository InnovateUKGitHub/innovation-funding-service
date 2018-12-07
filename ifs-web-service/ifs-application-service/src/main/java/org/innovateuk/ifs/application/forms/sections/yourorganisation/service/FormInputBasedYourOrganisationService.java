package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.GrowthTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.ORGANISATION_TURNOVER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;

/**
 * TODO DW - document this class
 */
@Service
public class FormInputBasedYourOrganisationService implements YourOrganisationService {

    private CompetitionRestService competitionRestService;
    private QuestionRestService questionRestService;
    private FormInputRestService formInputRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private ApplicationRestService applicationRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;
    private OrganisationRestService organisationRestService;

    public FormInputBasedYourOrganisationService(CompetitionRestService competitionRestService,
                                                 QuestionRestService questionRestService,
                                                 FormInputRestService formInputRestService,
                                                 FormInputResponseRestService formInputResponseRestService,
                                                 ApplicationRestService applicationRestService,
                                                 ApplicationFinanceRestService applicationFinanceRestService,
                                                 OrganisationRestService organisationRestService) {

        this.competitionRestService = competitionRestService;
        this.questionRestService = questionRestService;
        this.formInputRestService = formInputRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.applicationRestService = applicationRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.organisationRestService = organisationRestService;
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
    public ServiceResult<Boolean> isShowStateAidAgreement(long applicationId, long organisationId) {

        return getStateAidEligibilityForCompetition(applicationId).andOnSuccess(eligibility -> {
            if (!eligibility) {
                return serviceSuccess(false);
            }
            return isBusinessOrganisation(organisationId);
        });
    }

    private ServiceResult<Boolean> getStateAidEligibilityForCompetition(long applicationId) {
        CompetitionResource competition =
                applicationRestService.getCompetitionByApplicationId(applicationId).getSuccess();

        return serviceSuccess(TRUE.equals(competition.getStateAid()));
    }

    private ServiceResult<Boolean> isBusinessOrganisation(Long organisationId) {
        return organisationRestService.getOrganisationById(organisationId).
                andOnSuccessReturn(organisation -> organisation.getOrganisationType() == OrganisationTypeEnum.BUSINESS.getId()).
                toServiceResult();
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

    @Override
    public ServiceResult<Void> updateFinancialYearEnd(long applicationId, long competitionId, long userId, LocalDate financialYearEnd) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.ORGANISATION_TURNOVER);
    }

    @Override
    public ServiceResult<Boolean> isIncludingGrowthTable(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).
                andOnSuccessReturn(CompetitionResource::getIncludeProjectGrowthTable).
                toServiceResult();
    }

    @Override
    public ServiceResult<LocalDate> getFinancialYearEnd(long applicationId, long competitionId, long organisationId) {
        return getLocalDateValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.FINANCIAL_YEAR_END);
    }

    @Override
    public ServiceResult<List<GrowthTableRow>> getGrowthTableRows(long applicationId, long competitionId, long organisationId) {
        return serviceSuccess(emptyList());
    }

    @Override
    public ServiceResult<Long> getHeadCountAtLastFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.FINANCIAL_STAFF_COUNT);
    }

    private ServiceResult<Long> getLongValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses).
                toServiceResult();
    }

    private ServiceResult<LocalDate> getLocalDateValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getLocalDateValueFromFormInputResponses).
                toServiceResult();
    }

    private ServiceResult<Void> updateLongValueForFormInput(long applicationId, long competitionId, long userId, Long value, FormInputType formInputType) {
        return updateValueForFormInput(applicationId, competitionId, userId, value != null ? value.toString() : null, formInputType);
    }

    private ServiceResult<Void> updateLocalDateValueForFormInput(long applicationId, long competitionId, long userId, LocalDate value, FormInputType formInputType) {
        return updateValueForFormInput(applicationId, competitionId, userId, value != null ? value.toString() : null, formInputType);
    }

    private ServiceResult<Void> updateValueForFormInput(long applicationId, long competitionId, long userId, String stringValue, FormInputType formInputType) {

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

    private LocalDate getLocalDateValueFromFormInputResponses(Optional<FormInputResponseResource> formInputResponse) {

        return formInputResponse.
                map(response -> {
                    try {
                        return LocalDate.parse(response.getValue());
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }).
                orElse(null);
    }
}
