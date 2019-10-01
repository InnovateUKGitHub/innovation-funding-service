package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.Boolean.TRUE;
import static java.time.YearMonth.parse;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.ORGANISATION_TURNOVER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirstMandatory;

@Service
public class OrganisationFinanceServiceImpl extends BaseTransactionalService implements OrganisationFinanceService {

    private static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("MM-uuuu");

    static final String ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION = "Annual turnover";
    static final String ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION = "Annual profits";
    static final String ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION = "Annual export";
    static final String RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION = "Research and development spend";

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private FormInputService formInputService;
    @Autowired
    private FormInputResponseService formInputResponseService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationFinanceService financeService;
    @Autowired
    private ApplicationFinanceRowService financeRowCostsService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private AuthenticationHelper authenticationHelper;
    @Autowired
    private GrantClaimMaximumService grantClaimMaximumService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private UsersRolesService usersRolesService;
    @Autowired
    private SectionStatusService sectionStatusService;

    @Override
    public ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationWithGrowthTable(long applicationId, long organisationId) {
        long competitionId = getCompetitionId(applicationId);

        Boolean stateAidAgreed = getStateAidAgreed(applicationId).getSuccess();

        OrganisationSize organisationSize = getOrganisationSize(applicationId, organisationId).getSuccess();

        YearMonth financialYearEnd = getFinancialYearEnd(applicationId, competitionId, organisationId).getSuccess();

        Long annualTurnoverAtEndOfFinancialYear = getAnnualTurnoverAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long annualProfitsAtEndOfFinancialYear = getAnnualProfitsAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long annualExportAtEndOfFinancialYear = getAnnualExportAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long researchAndDevelopmentSpendAtEndOfFinancialYear = getResearchAndDevelopmentSpendAtEndOfFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        Long headCountAtLastFinancialYear = getHeadCountAtLastFinancialYear(applicationId, competitionId, organisationId).getSuccess();

        return serviceSuccess(new OrganisationFinancesWithGrowthTableResource(
                organisationSize,
                stateAidAgreed,
                financialYearEnd,
                headCountAtLastFinancialYear,
                annualTurnoverAtEndOfFinancialYear,
                annualProfitsAtEndOfFinancialYear,
                annualExportAtEndOfFinancialYear,
                researchAndDevelopmentSpendAtEndOfFinancialYear));
    }

    @Override
    public ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationWithoutGrowthTable(long applicationId, long organisationId) {
        long competitionId = getCompetitionId(applicationId);

        Boolean stateAidAgreed = getStateAidAgreed(applicationId).getSuccess();
        OrganisationSize organisationSize = getOrganisationSize(applicationId, organisationId).getSuccess();
        Long turnover = getTurnover(applicationId, competitionId, organisationId).getSuccess();
        Long headCount = getHeadCount(applicationId, competitionId, organisationId).getSuccess();

        return serviceSuccess(new OrganisationFinancesWithoutGrowthTableResource(organisationSize, turnover, headCount, stateAidAgreed));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateOrganisationWithGrowthTable(long applicationId, long organisationId, OrganisationFinancesWithGrowthTableResource finances) {
        long competitionId = getCompetitionId(applicationId);
        long userId = authenticationHelper.getCurrentlyLoggedInUser().getSuccess().getId();

        boolean stateAidIncluded = isShowStateAidAgreement(applicationId, organisationId).getSuccess();

        updateOrganisationSize(applicationId, competitionId, organisationId, finances.getOrganisationSize()).getSuccess();
        updateFinancialYearEnd(applicationId, competitionId, userId, finances.getFinancialYearEnd()).getSuccess();
        updateAnnualTurnoverAtEndOfFinancialYear(applicationId, competitionId, userId, finances.getAnnualTurnoverAtLastFinancialYear()).getSuccess();
        updateAnnualProfitsAtEndOfFinancialYear(applicationId, competitionId, userId, finances.getAnnualProfitsAtLastFinancialYear()).getSuccess();
        updateAnnualExportAtEndOfFinancialYear(applicationId, competitionId, userId, finances.getAnnualExportAtLastFinancialYear()).getSuccess();
        updateResearchAndDevelopmentSpendAtEndOfFinancialYear(applicationId, competitionId, userId, finances.getResearchAndDevelopmentSpendAtLastFinancialYear()).getSuccess();
        updateHeadCountAtEndOfFinancialYear(applicationId, competitionId, userId, finances.getHeadCountAtLastFinancialYear()).getSuccess();

        if (stateAidIncluded) {
            updateStateAidAgreed(applicationId, finances.getStateAidAgreed()).getSuccess();
        }

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateOrganisationWithoutGrowthTable(long applicationId, long organisationId, OrganisationFinancesWithoutGrowthTableResource finances) {
        long competitionId = getCompetitionId(applicationId);
        long userId = authenticationHelper.getCurrentlyLoggedInUser().getSuccess().getId();
        boolean stateAidIncluded = isShowStateAidAgreement(applicationId, organisationId).getSuccess();

        updateOrganisationSize(applicationId, competitionId, organisationId, finances.getOrganisationSize()).getSuccess();
        updateHeadCount(applicationId, competitionId, userId, finances.getHeadCount()).getSuccess();
        updateTurnover(applicationId, competitionId, userId, finances.getTurnover()).getSuccess();

        if (stateAidIncluded) {
            updateStateAidAgreed(applicationId, finances.getStateAidAgreed()).getSuccess();
        }

        return serviceSuccess();
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

    private long getCompetitionId(long applicationId) {
        ApplicationResource application = applicationService.getApplicationById(applicationId).getSuccess();

        return application.getCompetition();
    }


    private ServiceResult<Long> getTurnover(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, ORGANISATION_TURNOVER);
    }

    private ServiceResult<Long> getHeadCount(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.STAFF_COUNT);
    }

    private ServiceResult<Boolean> getStateAidAgreed(long applicationId) {
        return applicationService.getApplicationById(applicationId).
                andOnSuccessReturn(ApplicationResource::getStateAidAgreed);
    }

    private ServiceResult<Boolean> getStateAidEligibilityForCompetition(long applicationId) {
        return applicationService.getCompetitionByApplicationId(applicationId).
                andOnSuccessReturn(competition -> TRUE.equals(competition.getStateAid()));
    }

    private ServiceResult<Boolean> isBusinessOrganisation(Long organisationId) {
        return organisationService.findById(organisationId).
                andOnSuccessReturn(organisation -> organisation.getOrganisationType() == OrganisationTypeEnum.BUSINESS.getId());
    }

    private ServiceResult<OrganisationSize> getOrganisationSize(long applicationId, long organisationId) {
        return financeService.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId).
                andOnSuccessReturn(ApplicationFinanceResource::getOrganisationSize);
    }

    private ServiceResult<Void> updateTurnover(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.ORGANISATION_TURNOVER);
    }

    private ServiceResult<Void> updateHeadCount(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.STAFF_COUNT);
    }

    private ServiceResult<Void> updateStateAidAgreed(long applicationId, boolean stateAidAgreed) {
        return applicationService.getApplicationById(applicationId).
                andOnSuccess(application -> {
                    application.setStateAidAgreed(stateAidAgreed);
                    return applicationService.saveApplicationDetails(applicationId, application);
                }).
                andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> updateOrganisationSize(long applicationId, long competitionId, long organisationId, OrganisationSize organisationSize) {
        return financeService.financeDetails(applicationId, organisationId).
                andOnSuccess(finance -> {
                    if (finance.getOrganisationSize() != organisationSize) {

                        finance.setOrganisationSize(organisationSize);
                        ServiceResult<ApplicationFinanceResource> updateSizeResult = financeService.updateApplicationFinance(finance.getId(), finance);

                        long userId = authenticationHelper.getCurrentlyLoggedInUser().getSuccess().getId();
                        handleOrganisationSizeChange(finance, competitionId, userId);

                        return updateSizeResult;
                    } else {
                        return serviceSuccess(finance);
                    }

                }).
                andOnSuccessReturnVoid();
    }

    private void handleOrganisationSizeChange(ApplicationFinanceResource applicationFinance,
                                              long competitionId,
                                              long userId) {
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();
        OrganisationResource organisation = organisationService.findById(applicationFinance.getOrganisation()).getSuccess();
        boolean maximumFundingLevelOverridden = grantClaimMaximumService.isMaximumFundingLevelOverridden(competitionId).getSuccess();

        if (!competition.isMaximumFundingLevelConstant(organisation.getOrganisationTypeEnum(), maximumFundingLevelOverridden)) {
            resetFundingAndMarkAsIncomplete(applicationFinance, competitionId, userId);
        }
    }

    public void resetFundingAndMarkAsIncomplete(ApplicationFinanceResource applicationFinance, Long competitionId, Long userId) {
        final ProcessRoleResource processRole =
                usersRolesService.getAssignableProcessRolesByApplicationId(applicationFinance.getApplication()).getSuccess().stream()
                        .filter(processRoleResource -> userId.equals(processRoleResource.getUser()))
                        .findFirst().get();

        sectionService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FUNDING_FINANCES).getSuccess()
                .forEach(fundingSection ->
                        sectionStatusService.markSectionAsInComplete(
                                fundingSection.getId(),
                                applicationFinance.getApplication(),
                                processRole.getId()
                        ));

        resetFundingLevel(applicationFinance);
    }

    private void resetFundingLevel(ApplicationFinanceResource applicationFinance) {
        GrantClaim grantClaim = applicationFinance.getGrantClaim();
        grantClaim.reset();
        financeRowCostsService.update(grantClaim.getId(), grantClaim).getSuccess();
    }

    private ServiceResult<Void> updateFinancialYearEnd(long applicationId, long competitionId, long userId, YearMonth financialYearEnd) {
        return updateYearMonthValueForFormInput(applicationId, competitionId, userId, financialYearEnd, FormInputType.FINANCIAL_YEAR_END);
    }

    private ServiceResult<YearMonth> getFinancialYearEnd(long applicationId, long competitionId, long organisationId) {
        return getYearMonthValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.FINANCIAL_YEAR_END);
    }

    private ServiceResult<Long> getAnnualTurnoverAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Long> getAnnualProfitsAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Long> getAnnualExportAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Long> getResearchAndDevelopmentSpendAtEndOfFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputTypeAndDescription(applicationId, competitionId, organisationId,
                FormInputType.FINANCIAL_OVERVIEW_ROW, RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Long> getHeadCountAtLastFinancialYear(long applicationId, long competitionId, long organisationId) {
        return getLongValueForFormInputType(applicationId, competitionId, organisationId, FormInputType.FINANCIAL_STAFF_COUNT);
    }

    private ServiceResult<Long> getLongValueForFormInputTypeAndDescription(long applicationId,
                                                                           long competitionId,
                                                                           long organisationId,
                                                                           FormInputType formInputType,
                                                                           String description) {

        return getFormInputResponseForOrganisationByDescription(applicationId, competitionId, organisationId, formInputType, description).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses);
    }

    private ServiceResult<Long> getLongValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getLongValueFromFormInputResponses);
    }

    private ServiceResult<YearMonth> getYearMonthValueForFormInputType(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getFormInputResponseForOrganisation(applicationId, competitionId, organisationId, formInputType).
                andOnSuccessReturn(this::getYearMonthValueFromFormInputResponses);
    }

    private ServiceResult<Void> updateAnnualTurnoverAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Void> updateAnnualProfitsAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Void> updateAnnualExportAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Void> updateResearchAndDevelopmentSpendAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInputAndDescription(applicationId, competitionId, userId, value,
                FormInputType.FINANCIAL_OVERVIEW_ROW, RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION);
    }

    private ServiceResult<Void> updateHeadCountAtEndOfFinancialYear(long applicationId, long competitionId, long userId, Long value) {
        return updateLongValueForFormInput(applicationId, competitionId, userId, value, FormInputType.FINANCIAL_STAFF_COUNT);
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
                andOnSuccess(question -> formInputService.findByQuestionId(question.getId())).
                andOnSuccessReturn(formInputs -> simpleFindFirstMandatory(formInputs, correctFormInputTest)).
                andOnSuccessReturn(formInput -> formInputResponseService.saveQuestionResponse(
                        new FormInputResponseCommand(formInput.getId(), applicationId, userId, stringValue))).
                andOnSuccessReturnVoid();
    }

    private ServiceResult<Optional<FormInputResponseResource>> getFormInputResponseForOrganisation(long applicationId, long competitionId, long organisationId, FormInputType formInputType) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputResponseService.findResponseByApplicationIdQuestionIdOrganisationIdAndFormInputType(applicationId, question.getId(), organisationId, formInputType)).
                toOptionalIfNotFound();
    }

    private ServiceResult<Optional<FormInputResponseResource>> getFormInputResponseForOrganisationByDescription(long applicationId, long competitionId, long organisationId, FormInputType formInputType, String description) {
        return getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType).
                andOnSuccess(question -> formInputResponseService.findResponseByApplicationIdQuestionIdOrganisationIdFormInputTypeAndDescription(applicationId, question.getId(), organisationId, formInputType, description)).
                toOptionalIfNotFound();
    }

    private ServiceResult<Question> getQuestionByCompetitionIdAndFormInputType(long competitionId, FormInputType formInputType) {
        return questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType);
    }

    private Long getLongValueFromFormInputResponses(Optional<FormInputResponseResource> formInputResponse) {
        return formInputResponse
                .map(response -> isDigits(response.getValue()) ? Long.valueOf(response.getValue()) : null)
                .orElse(null);
    }

    private YearMonth getYearMonthValueFromFormInputResponses(Optional<FormInputResponseResource> formInputResponse) {
        return formInputResponse
                .map(this::getYearMonthFromForm)
                .orElse(null);
    }

    private YearMonth getYearMonthFromForm(FormInputResponseResource formInputResponseResource) {
        try {
            return parse(formInputResponseResource.getValue(), MONTH_YEAR_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}