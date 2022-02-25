package org.innovateuk.ifs.finance.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Slf4j
public abstract class AbstractOrganisationFinanceService<Finance extends BaseFinanceResource> extends BaseTransactionalService implements OrganisationFinanceService {

    @Value("${ifs.ktp.phase2.enabled}")
    private Boolean ktpPhase2Enabled;

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private AuthenticationHelper authenticationHelper;
    @Autowired
    private GrantClaimMaximumService grantClaimMaximumService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private FormInputService formInputService;
    @Autowired
    private FormInputResponseService formInputResponseService;

    protected abstract ServiceResult<Finance> getFinance(long targetId, long organisationId);
    protected abstract ServiceResult<Finance> updateFinance(Finance finance);
    protected abstract ServiceResult<FinanceRowItem> saveGrantClaim(GrantClaim grantClaim);
    protected abstract ServiceResult<CompetitionResource> getCompetitionFromTargetId(long targetId);
    protected abstract void resetYourFundingSection(Finance finance, long competitionId, long userId);

    @Override
    public ServiceResult<OrganisationFinancesWithGrowthTableResource> getOrganisationWithGrowthTable(long targetId, long organisationId) {
        Finance finance = getFinance(targetId, organisationId).getSuccess();

        OrganisationSize organisationSize = finance.getOrganisationSize();

        Optional<GrowthTableResource> growthTable = ofNullable(finance.getFinancialYearAccounts())
                .filter(GrowthTableResource.class::isInstance)
                .map(GrowthTableResource.class::cast);

        YearMonth financialYearEnd = growthTable.map(GrowthTableResource::getFinancialYearEnd)
                .map(YearMonth::from)
                .orElse(null);
        BigDecimal annualTurnoverAtEndOfFinancialYear = growthTable.map(GrowthTableResource::getAnnualTurnover)
                .orElse(null);
        BigDecimal annualProfitsAtEndOfFinancialYear = growthTable.map(GrowthTableResource::getAnnualProfits)
                .orElse(null);
        BigDecimal annualExportAtEndOfFinancialYear = growthTable.map(GrowthTableResource::getAnnualExport)
                .orElse(null);
        BigDecimal researchAndDevelopmentSpendAtEndOfFinancialYear = growthTable.map(GrowthTableResource::getResearchAndDevelopment)
                .orElse(null);
        Long headCountAtLastFinancialYear = growthTable.map(GrowthTableResource::getEmployees)
                .orElse(null);

        return serviceSuccess(new OrganisationFinancesWithGrowthTableResource(
                organisationSize,
                financialYearEnd,
                headCountAtLastFinancialYear,
                annualTurnoverAtEndOfFinancialYear,
                annualProfitsAtEndOfFinancialYear,
                annualExportAtEndOfFinancialYear,
                researchAndDevelopmentSpendAtEndOfFinancialYear));
    }

    @Override
    public ServiceResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationWithoutGrowthTable(long targetId, long organisationId) {
        Finance finance = getFinance(targetId, organisationId).getSuccess();

        OrganisationSize organisationSize = finance.getOrganisationSize();

        Optional<EmployeesAndTurnoverResource> employeesAndTurnover = ofNullable(finance.getFinancialYearAccounts())
                .filter(EmployeesAndTurnoverResource.class::isInstance)
                .map(EmployeesAndTurnoverResource.class::cast);

        BigDecimal turnover = employeesAndTurnover.map(EmployeesAndTurnoverResource::getTurnover).orElse(null);
        Long headCount = employeesAndTurnover.map(EmployeesAndTurnoverResource::getEmployees).orElse(null);

        return serviceSuccess(new OrganisationFinancesWithoutGrowthTableResource(organisationSize, turnover,
            headCount));
    }

    @Override
    public ServiceResult<OrganisationFinancesKtpYearsResource> getOrganisationKtpYears(long targetId, long organisationId) {
        Finance finance = getFinance(targetId, organisationId).getSuccess();

        OrganisationSize organisationSize = finance.getOrganisationSize();

        Optional<KtpYearsResource> ktpYears = ofNullable(finance.getFinancialYearAccounts())
                .filter(KtpYearsResource.class::isInstance)
                .map(KtpYearsResource.class::cast);

        Optional<FormInputResource> formInputResource = getKtpYearsAdditionalInfomationFormInput(targetId);
        Optional<FormInputResponseResource> additionalInfo = ktpPhase2Enabled ? (
                formInputResource.isPresent() ? formInputResponseService.findResponsesByFormInputIdAndApplicationId(formInputResource.get().getId(), targetId).getSuccess().stream().findFirst() : Optional.ofNullable(null)
                ) : Optional.ofNullable(null);

        return serviceSuccess(new OrganisationFinancesKtpYearsResource(
                additionalInfo.isPresent() ? additionalInfo.get().getUpdatedByUser() : null,
                organisationSize,
                ktpYears.map(KtpYearsResource::getYears).orElse(Collections.emptyList()),
                formInputResource.isPresent(),
                additionalInfo.isPresent() ? additionalInfo.get().getValue() : null,
                ktpYears.map(KtpYearsResource::getGroupEmployees).orElse(null),
                ktpYears.map(KtpYearsResource::getFinancialYearEnd)
                        .map(YearMonth::from)
                        .orElse(null)));
    }

    private Optional<FormInputResource> getKtpYearsAdditionalInfomationFormInput(long targetId) {
        CompetitionResource competitionResource = competitionService.getCompetitionByApplicationId(targetId).getSuccess();
        Optional<SectionResource> sectionResource = sectionService.getSectionsByCompetitionIdAndType(competitionResource.getId(), SectionType.ORGANISATION_FINANCES).getSuccess().stream().findFirst();
        Optional<QuestionResource> questionResource = questionService.getQuestionsBySectionIdAndType(sectionResource.get().getId(), QuestionType.GENERAL).getSuccess().stream().findFirst();
        return formInputService.findByQuestionId(questionResource.get().getId()).getSuccess().stream().findFirst();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateOrganisationWithGrowthTable(long targetId, long organisationId, OrganisationFinancesWithGrowthTableResource finances) {
        long competitionId = getCompetitionId(targetId);

        // finance
        Finance finance = getFinance(targetId, organisationId).getSuccess();
        updateOrganisationSize(finance, competitionId, finances.getOrganisationSize());
        GrowthTableResource growthTable = (GrowthTableResource) finance.getFinancialYearAccounts();
        growthTable.setFinancialYearEnd(ofNullable(finances.getFinancialYearEnd()).map(YearMonth::atEndOfMonth).orElse(null));
        growthTable.setAnnualTurnover(finances.getAnnualTurnoverAtLastFinancialYear());
        growthTable.setAnnualProfits(finances.getAnnualProfitsAtLastFinancialYear());
        growthTable.setAnnualExport(finances.getAnnualExportAtLastFinancialYear());
        growthTable.setResearchAndDevelopment(finances.getResearchAndDevelopmentSpendAtLastFinancialYear());
        growthTable.setEmployees(finances.getHeadCountAtLastFinancialYear());

        updateFinance(finance).getSuccess();

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateOrganisationWithoutGrowthTable(long targetId, long organisationId, OrganisationFinancesWithoutGrowthTableResource finances) {
        long competitionId = getCompetitionId(targetId);

        //finance
        Finance finance = getFinance(targetId, organisationId).getSuccess();
        updateOrganisationSize(finance, competitionId, finances.getOrganisationSize());
        EmployeesAndTurnoverResource employeesAndTurnover = (EmployeesAndTurnoverResource) finance.getFinancialYearAccounts();
        employeesAndTurnover.setTurnover(finances.getTurnover());
        employeesAndTurnover.setEmployees(finances.getHeadCount());

        updateFinance(finance).getSuccess();

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateOrganisationKtpYears(long targetId, long organisationId, OrganisationFinancesKtpYearsResource finances) {
        long competitionId = getCompetitionId(targetId);

        // finance
        Finance finance = getFinance(targetId, organisationId).getSuccess();
        updateOrganisationSize(finance, competitionId, finances.getOrganisationSize());
        KtpYearsResource ktpYearsResource = (KtpYearsResource) finance.getFinancialYearAccounts();
        ktpYearsResource.setYears(finances.getYears());
        ktpYearsResource.setGroupEmployees(finances.getGroupEmployees());
        ktpYearsResource.setFinancialYearEnd(ofNullable(finances.getFinancialYearEnd()).map(YearMonth::atEndOfMonth).orElse(null));

        updateFinance(finance).getSuccess();

        if(ktpPhase2Enabled) {
            getKtpYearsAdditionalInfomationFormInput(targetId).ifPresent(formInputResource ->
            {
                updateKtpYearsAdditionalInfo(targetId, formInputResource, finances).getSuccess();
            });
        }

        return serviceSuccess();
    }

    private ServiceResult<FormInputResponseResource> updateKtpYearsAdditionalInfo(long targetId, FormInputResource formInputResource, OrganisationFinancesKtpYearsResource finances) {
        return formInputResponseService.saveQuestionResponse(new FormInputResponseCommand(formInputResource.getId(), targetId, finances.getUserId(), finances.getAdditionalInfo(), null));
    }

    private ServiceResult<Boolean> getAidEligibilityForCompetition(long targetId) {
        return getCompetitionFromTargetId(targetId).
                andOnSuccessReturn(competition ->
                        competition.getFundingRules() != null
                        && FundingRules.NOT_AID != competition.getFundingRules()
                );
    }

    private ServiceResult<Boolean> isBusinessOrganisation(Long organisationId) {
        return organisationService.findById(organisationId).
                andOnSuccessReturn(organisation -> organisation.getOrganisationType() == OrganisationTypeEnum.BUSINESS.getId());
    }

    private void updateOrganisationSize(Finance finance, long competitionId, OrganisationSize organisationSize) {
        if (finance.getOrganisationSize() != organisationSize) {
            finance.setOrganisationSize(organisationSize);
            long userId = authenticationHelper.getCurrentlyLoggedInUser().getSuccess().getId();
            handleOrganisationSizeChange(finance, competitionId, userId);
        }
    }

    private void handleOrganisationSizeChange(Finance finance,
                                              long competitionId,
                                              long userId) {
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();

        if (!competition.isMaximumFundingLevelConstant(() -> organisationService.findById(finance.getOrganisation()).getSuccess().getOrganisationTypeEnum(),
                () -> grantClaimMaximumService.isMaximumFundingLevelConstant(competitionId).getSuccess())) {
            resetYourFundingSection(finance, competitionId, userId);
            resetFundingLevel(finance);
        }
    }

    private void resetFundingLevel(Finance finance) {
        GrantClaim grantClaim = finance.getGrantClaim();
        grantClaim.reset();
        saveGrantClaim(grantClaim).getSuccess();
    }

    private long getCompetitionId(long targetId) {
        return getCompetitionFromTargetId(targetId).getSuccess().getId();
    }
}