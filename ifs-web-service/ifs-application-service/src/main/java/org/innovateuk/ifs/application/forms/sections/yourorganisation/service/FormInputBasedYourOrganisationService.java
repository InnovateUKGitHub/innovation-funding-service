package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.ORGANISATION_TURNOVER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;

/**
 * TODO DW - document this class
 */
@Service
public class FormInputBasedYourOrganisationService implements YourOrganisationService {

    private static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("MM-uuuu");

    private static final String ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION = "Annual turnover";
    private static final String ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION = "Annual profits";
    private static final String ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION = "Annual export";
    private static final String RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION = "Research and development spend";

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
        return applicationRestService.getCompetitionByApplicationId(applicationId).
                toServiceResult().
                andOnSuccessReturn(competition -> TRUE.equals(competition.getStateAid()));
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
    public ServiceResult<Void> updateFinancialYearEnd(long applicationId, long competitionId, long userId, YearMonth financialYearEnd) {
        return updateYearMonthValueForFormInput(applicationId, competitionId, userId, financialYearEnd, FormInputType.FINANCIAL_YEAR_END);
    }

    @Override
    public ServiceResult<Boolean> isIncludingGrowthTable(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).
                andOnSuccessReturn(competition -> TRUE.equals(competition.getIncludeProjectGrowthTable())).
                toServiceResult();
    }

    @Override
    public ServiceResult<YearMonth> getFinancialYearEnd(long applicationId, long competitionId, long organisationId) {
        return getYearMonthValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.FINANCIAL_YEAR_END);
    }

    @Override
    public ServiceResult<Long> getAnnualTurnoverAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Long> getAnnualProfitsAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Long> getAnnualExportAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Long> getResearchAndDevelopmentSpendAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Long> getHeadCountAtLastFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.FINANCIAL_STAFF_COUNT);
    }

    @Override
    public ServiceResult<Void> updateAnnualTurnoverAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Void> updateAnnualProfitsAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Void> updateAnnualExportAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Void> updateResearchAndDevelopmentSpendAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION);
    }

    @Override
    public ServiceResult<Void> updateHeadCountAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.FINANCIAL_STAFF_COUNT);
    }

    private ServiceResult<Long> getLongValueForFormInputTypeAndDescription(long applicationId,
                                                                           long competitionId,
                                                                           long organisationId,
                                                                           FormInputType formInputType,
                                                                           String description) {

        return getFormInputResponseForOrganisationByDescription(applicationId, competitionId, organisationId, formInputType, description).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses).
                toServiceResult();
    }

    private ServiceResult<Long> getLongValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses).
                toServiceResult();
    }

    private ServiceResult<YearMonth> getYearMonthValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getYearMonthValueFromFormInputResponses).
                toServiceResult();
    }

    private ServiceResult<Void> updateLongValueForFormInput(long applicationId, long competitionId, long userId, Long value, FormInputType formInputType) {
        return updateValueForFormInput(applicationId, competitionId, userId, value != null ? value.toString() : null, formInputType);
    }

    private ServiceResult<Void> updateLongValueForFormInputAndDescription(long applicationId, long competitionId, long userId, Long value, FormInputType formInputType, String description) {
        return updateValueForFormInputAndDescription(applicationId, competitionId, userId, value != null ? value.toString() : null, formInputType, description);
    }

    private ServiceResult<Void> updateYearMonthValueForFormInput(long applicationId, long competitionId, long userId, YearMonth value, FormInputType formInputType) {
        return updateValueForFormInput(applicationId, competitionId, userId,
                value != null ? value.format(MONTH_YEAR_FORMAT) : null, formInputType);
    }

    private ServiceResult<Void> updateValueForFormInput(long applicationId, long competitionId, long userId, String stringValue, FormInputType formInputType) {

        Predicate<FormInputResource> matchingType = fi -> formInputType.equals(fi.getType());

        return updateValueForFormInput(applicationId, competitionId, userId, stringValue, formInputType,
                matchingType);
    }

    private ServiceResult<Void> updateValueForFormInputAndDescription(long applicationId, long competitionId, long userId, String stringValue, FormInputType formInputType, String description) {

        Predicate<FormInputResource> matchingTypeAndDescription = fi -> formInputType.equals(fi.getType()) && description.equals(fi.getDescription());

        return updateValueForFormInput(applicationId, competitionId, userId, stringValue, formInputType,
                matchingTypeAndDescription);
    }

    private ServiceResult<Void> updateValueForFormInput(long applicationId, long competitionId, long userId, String stringValue, FormInputType formInputType, Predicate<FormInputResource> correctFormInputTest) {

        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputRestService.getByQuestionId(question.getId())).
                andOnSuccessReturn(formInputs -> simpleFindFirstMandatory(formInputs, correctFormInputTest::test)).
                andOnSuccessReturn(formInput -> formInputResponseRestService.saveQuestionResponse(
                        userId,
                        applicationId,
                        formInput.getId(),
                        stringValue,
                        false)).
                toServiceResult().
                andOnSuccessReturnVoid();
    }

    private RestResult<Optional<FormInputResponseResource>> getFormInputResponseForOrganisation(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputResponseRestService.getByApplicationIdQuestionIdOrganisationIdAndFormInputType(applicationId, question.getId(), organisationId, formInputType)).
                toOptionalIfNotFound();
    }

    private RestResult<Optional<FormInputResponseResource>> getFormInputResponseForOrganisationByDescription(long applicationId, long competitionId, long organisationId, FormInputType formInputType, String description) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputResponseRestService.getByApplicationIdQuestionIdOrganisationIdFormInputTypeAndDescription(applicationId, question.getId(), organisationId, formInputType, description)).
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

    private YearMonth getYearMonthValueFromFormInputResponses(Optional<FormInputResponseResource> formInputResponse) {

        return formInputResponse.
                map(response -> {
                    try {
                        return YearMonth.parse(response.getValue(), MONTH_YEAR_FORMAT);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                }).
                orElse(null);
    }
}
