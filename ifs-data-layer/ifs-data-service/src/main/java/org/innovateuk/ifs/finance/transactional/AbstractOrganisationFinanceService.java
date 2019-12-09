package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public abstract class AbstractOrganisationFinanceService<Finance extends BaseFinanceResource> extends BaseTransactionalService implements OrganisationFinanceService {

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private AuthenticationHelper authenticationHelper;
    @Autowired
    private GrantClaimMaximumService grantClaimMaximumService;

    protected abstract ServiceResult<Finance> getFinance(long targetId, long organisationId);
    protected abstract ServiceResult<Void> updateFinance(Finance finance);
    protected abstract ServiceResult<FinanceRowItem> saveGrantClaim(GrantClaim grantClaim);
    protected abstract ServiceResult<CompetitionResource> getCompetitionFromTargetId(long targetId);
    protected abstract void resetYourFundingSection(Finance finance, long competitionId, long userId);
    protected abstract ServiceResult<Void> updateStateAidAgreed(long targetId);

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
    public ServiceResult<Boolean> isShowStateAidAgreement(long targetId, long organisationId) {
        return getStateAidEligibilityForCompetition(targetId).andOnSuccess(eligibility -> {
            if (!eligibility) {
                return serviceSuccess(false);
            }
            return isBusinessOrganisation(organisationId);
        });
    }


    private ServiceResult<Boolean> getStateAidEligibilityForCompetition(long targetId) {
        return getCompetitionFromTargetId(targetId).
                andOnSuccessReturn(competition -> TRUE.equals(competition.getStateAid()));
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
        OrganisationResource organisation = organisationService.findById(finance.getOrganisation()).getSuccess();
        boolean maximumFundingLevelOverridden = grantClaimMaximumService.isMaximumFundingLevelOverridden(competitionId).getSuccess();

        if (!competition.isMaximumFundingLevelConstant(organisation.getOrganisationTypeEnum(), maximumFundingLevelOverridden)) {
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