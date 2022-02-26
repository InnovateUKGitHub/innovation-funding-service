package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
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
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.any;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.EmployeesAndTurnoverResourceBuilder.newEmployeesAndTurnoverResource;
import static org.innovateuk.ifs.finance.builder.GrowthTableResourceBuilder.newGrowthTableResource;
import static org.innovateuk.ifs.finance.builder.KtpYearResourceBuilder.newKtpYearResource;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesKtpYearsResourceBuilder.newOrganisationFinancesKtpYearsResource;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesWithGrowthTableResourceBuilder.newOrganisationFinancesWithGrowthTableResource;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesWithoutGrowthTableResourceBuilder.newOrganisationFinancesWithoutGrowthTableResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractOrganisationFinanceServiceTest extends BaseServiceUnitTest<AbstractOrganisationFinanceService> {

    @Mock
    private BaseFinanceResource finance;
    @Mock
    private OrganisationService organisationService;
    @Mock
    private CompetitionService competitionService;
    @Mock
    private GrantClaimMaximumService grantClaimMaximumService;
    @Mock
    private SectionService sectionService;
    @Mock
    private QuestionService questionService;
    @Mock
    private FormInputService formInputService;
    @Mock
    private FormInputResponseService formInputResponseService;
    @Mock
    private AuthenticationHelper authenticationHelper;
    @Mock
    private GrantClaim grantClaim;

    private static final long organisationId = 1L;
    private static final long targetId = 2L;
    private static final long competitionId = 3L;
    private OrganisationResource organisation;
    private CompetitionResource competition;
    private FinanceRowItem financeRowItem;
    private OrganisationFinancesWithoutGrowthTableResource organisationFinancesWithoutGrowthTableResource;
    private OrganisationFinancesWithGrowthTableResource organisationFinancesWithGrowthTableResource;
    private EmployeesAndTurnoverResource employeesAndTurnoverResource;
    private GrowthTableResource growthTableResource;

    private SectionResource sectionResource;
    private QuestionResource questionResource;
    private FormInputResource formInputResource;

    @Before
    public void setup() {
        User user = newUser()
            .withId(11L)
            .build();
        organisation = newOrganisationResource()
            .withId(organisationId)
            .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
            .build();
        competition = newCompetitionResource()
            .withId(competitionId)
            .withFundingRules(FundingRules.STATE_AID)
            .build();
        organisationFinancesWithoutGrowthTableResource = newOrganisationFinancesWithoutGrowthTableResource()
            .withOrganisationSize(OrganisationSize.SMALL)
            .withTurnover(BigDecimal.valueOf(4))
            .withHeadCount(5L)
            .build();
        organisationFinancesWithGrowthTableResource = newOrganisationFinancesWithGrowthTableResource()
            .withOrganisationSize(OrganisationSize.SMALL)
            .withTurnover(BigDecimal.valueOf(6))
            .withHeadCount(7L)
            .withAnnualExport(BigDecimal.valueOf(8))
            .withAnnualProfits(BigDecimal.valueOf(9))
            .withFinancialYearEnd(YearMonth.now())
            .withResearchAndDevelopment(BigDecimal.valueOf(10))
            .build();
        employeesAndTurnoverResource = newEmployeesAndTurnoverResource()
            .build();
        growthTableResource = newGrowthTableResource()
            .build();

        when(finance.getOrganisation()).thenReturn(organisationId);
        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competition));
        when(organisationService.findById(organisationId)).thenReturn(serviceSuccess(organisation));
        when(authenticationHelper.getCurrentlyLoggedInUser()).thenReturn(serviceSuccess(user));

        sectionResource = newSectionResource().build();
        questionResource = newQuestionResource().build();
        formInputResource = newFormInputResource().build();
        when(competitionService.getCompetitionByApplicationId(targetId)).thenReturn(serviceSuccess(competition));
        when(sectionService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.ORGANISATION_FINANCES)).thenReturn(serviceSuccess(singletonList(sectionResource)));
        when(questionService.getQuestionsBySectionIdAndType(sectionResource.getId(), QuestionType.GENERAL)).thenReturn(serviceSuccess(singletonList(questionResource)));
        when(formInputService.findByQuestionId(questionResource.getId())).thenReturn(serviceSuccess(singletonList(formInputResource)));
    }

    @Override
    protected AbstractOrganisationFinanceService supplyServiceUnderTest() {
        AbstractOrganisationFinanceService service = new AbstractOrganisationFinanceService() {
            @Override
            protected ServiceResult getFinance(long targetId, long organisationId) {
                return serviceSuccess(finance);
            }

            @Override
            protected ServiceResult<Void> updateFinance(BaseFinanceResource baseFinanceResource) {
                return serviceSuccess();
            }

            @Override
            protected ServiceResult<FinanceRowItem> saveGrantClaim(GrantClaim grantClaim) {
                return serviceSuccess(financeRowItem);
            }

            @Override
            protected ServiceResult<CompetitionResource> getCompetitionFromTargetId(long targetId) {
                return serviceSuccess(competition);
            }

            @Override
            protected void resetYourFundingSection(BaseFinanceResource baseFinanceResource, long competitionId,
                                                   long userId) {
            }
        };
        ReflectionTestUtils.setField(service, "ktpPhase2Enabled", Boolean.TRUE);
        return service;
    }

    @Test
    public void updateOrganisationWithoutGrowthTable_whenStateAidIncludedAndAgreed() {
        when(finance.getFinancialYearAccounts()).thenReturn(employeesAndTurnoverResource);
        when(grantClaimMaximumService.isMaximumFundingLevelConstant(competitionId)).thenReturn(serviceSuccess(false));
        when(finance.getGrantClaim()).thenReturn(grantClaim);

        assertEquals(serviceSuccess(), service.updateOrganisationWithoutGrowthTable(targetId, organisationId,
            organisationFinancesWithoutGrowthTableResource));

        assertEquals(organisationFinancesWithoutGrowthTableResource.getTurnover(), employeesAndTurnoverResource.getTurnover());
        assertEquals(organisationFinancesWithoutGrowthTableResource.getHeadCount(), employeesAndTurnoverResource.getEmployees());
        verify(grantClaim).reset();
    }

    @Test
    public void updateOrganisationWithGrowthTable_whenStateAidIncludedAndAgreed() {
        when(finance.getFinancialYearAccounts()).thenReturn(growthTableResource);
        when(grantClaimMaximumService.isMaximumFundingLevelConstant(competitionId)).thenReturn(serviceSuccess(true));

        assertEquals(serviceSuccess(), service.updateOrganisationWithGrowthTable(targetId, organisationId,
            organisationFinancesWithGrowthTableResource));

        assertEquals(ofNullable(organisationFinancesWithGrowthTableResource.getFinancialYearEnd())
            .map(YearMonth::atEndOfMonth).orElse(null), growthTableResource.getFinancialYearEnd());
        assertEquals(organisationFinancesWithGrowthTableResource.getAnnualTurnoverAtLastFinancialYear(),
            growthTableResource.getAnnualTurnover());
        assertEquals(organisationFinancesWithGrowthTableResource.getAnnualProfitsAtLastFinancialYear(),
            growthTableResource.getAnnualProfits());
        assertEquals(organisationFinancesWithGrowthTableResource.getAnnualExportAtLastFinancialYear(),
            growthTableResource.getAnnualExport());
        assertEquals(organisationFinancesWithGrowthTableResource.getResearchAndDevelopmentSpendAtLastFinancialYear(),
            growthTableResource.getResearchAndDevelopment());
        assertEquals(organisationFinancesWithGrowthTableResource.getHeadCountAtLastFinancialYear(),
            growthTableResource.getEmployees());
    }

    @Test
    public void updateOrganisationKtpFinancialYears() {
        User user = newUser()
                .withId(11L)
                .build();
        OrganisationFinancesKtpYearsResource organisationFinancesKtpYearsResource =  newOrganisationFinancesKtpYearsResource()
                .withUserId(user.getId())
                .withOrganisationSize(OrganisationSize.SMALL)
                .withFinancialYearEnd(YearMonth.now().minusMonths(1))
                .withGroupEmployees(2L)
                .withKtpYears(newKtpYearResource()
                        .withYear(0,1,2)
                        .withTurnover(BigDecimal.valueOf(1))
                        .withPreTaxProfit(BigDecimal.valueOf(2))
                        .withCurrentAssets(BigDecimal.valueOf(3))
                        .withLiabilities(BigDecimal.valueOf(4))
                        .withShareholderValue(BigDecimal.valueOf(5))
                        .withLoans(BigDecimal.valueOf(6))
                        .withEmployees(7L)
                        .build(3))
                .build();
        KtpYearsResource yearsResource = new KtpYearsResource();
        when(finance.getFinancialYearAccounts()).thenReturn(yearsResource);
        when(grantClaimMaximumService.isMaximumFundingLevelConstant(competitionId)).thenReturn(serviceSuccess(false));
        when(finance.getGrantClaim()).thenReturn(grantClaim);

        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().build();
        when(formInputResponseService.saveQuestionResponse(any())).thenReturn(serviceSuccess(formInputResponseResource));

        assertEquals(serviceSuccess(), service.updateOrganisationKtpYears(targetId, organisationId,
                organisationFinancesKtpYearsResource));

        assertEquals(organisationFinancesKtpYearsResource.getYears(), yearsResource.getYears());
        assertEquals(organisationFinancesKtpYearsResource.getFinancialYearEnd(), YearMonth.from(yearsResource.getFinancialYearEnd()));
        assertEquals(organisationFinancesKtpYearsResource.getGroupEmployees(), yearsResource.getGroupEmployees());
        verify(grantClaim).reset();
    }
}
