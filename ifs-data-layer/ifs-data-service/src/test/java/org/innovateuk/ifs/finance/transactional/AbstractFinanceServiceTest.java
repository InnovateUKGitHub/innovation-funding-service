package org.innovateuk.ifs.finance.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.innovateuk.ifs.finance.builder.KtpYearResourceBuilder.newKtpYearResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractFinanceServiceTest extends BaseServiceUnitTest<AbstractFinanceService> {

    @Mock
    private Finance finance;

    @Mock
    private BaseFinanceResource financeResource;

    private Competition competition;

    @Override
    protected AbstractFinanceService supplyServiceUnderTest() {
        return new AbstractFinanceService() {
            @Override
            protected void updateFinancialYearData(Finance finance, BaseFinanceResource financeResource) {
                super.updateFinancialYearData(finance, financeResource);
            }
        };
    }

    @Before
    public void setup() {
        when(financeResource.getOrganisationSize()).thenReturn(OrganisationSize.SMALL);
        competition = new Competition();
        when(finance.getCompetition()).thenReturn(competition);
    }

    @Test
    public void updateFinanceDetails_IncludeProjectGrowthTable() {
        competition.setIncludeProjectGrowthTable(false);
        EmployeesAndTurnover employeesAndTurnover = new EmployeesAndTurnover();
        when(finance.getEmployeesAndTurnover()).thenReturn(employeesAndTurnover);

        EmployeesAndTurnoverResource employeesAndTurnoverResource = new EmployeesAndTurnoverResource();
        BigDecimal expectedTurnover = BigDecimal.valueOf(1);
        Long expectedEmployees = 2L;
        employeesAndTurnoverResource.setTurnover(expectedTurnover);
        employeesAndTurnoverResource.setEmployees(expectedEmployees);
        when(financeResource.getFinancialYearAccounts()).thenReturn(employeesAndTurnoverResource);

        service.updateFinancialYearData(finance, financeResource);

        assertEquals(expectedEmployees, employeesAndTurnover.getEmployees());
        assertEquals(expectedTurnover, employeesAndTurnover.getTurnover());
        verify(finance).setOrganisationSize(OrganisationSize.SMALL);
    }

    @Test
    public void updateFinanceDetails_NotIncludeProjectGrowthTable() {
        competition.setIncludeProjectGrowthTable(true);
        GrowthTable growthTable = new GrowthTable();
        when(finance.getGrowthTable()).thenReturn(growthTable);

        GrowthTableResource growthTableResource = new GrowthTableResource();
        BigDecimal expectedAnnualExport = BigDecimal.valueOf(3);
        BigDecimal expectedAnnualProfits = BigDecimal.valueOf(4);
        BigDecimal expectedAnnualTurnover = BigDecimal.valueOf(5);
        BigDecimal expectedResearchAndDevelopment = BigDecimal.valueOf(6);
        LocalDate expectedFinancialYearEnd = LocalDate.now();
        Long expectedEmployees = 7L;

        growthTableResource.setAnnualExport(expectedAnnualExport);
        growthTableResource.setAnnualProfits(expectedAnnualProfits);
        growthTableResource.setAnnualTurnover(expectedAnnualTurnover);
        growthTableResource.setResearchAndDevelopment(expectedResearchAndDevelopment);
        growthTableResource.setFinancialYearEnd(expectedFinancialYearEnd);
        growthTableResource.setEmployees(expectedEmployees);
        when(financeResource.getFinancialYearAccounts()).thenReturn(growthTableResource);

        service.updateFinancialYearData(finance, financeResource);

        assertEquals(expectedAnnualExport, growthTable.getAnnualExport());
        assertEquals(expectedAnnualProfits, growthTable.getAnnualProfits());
        assertEquals(expectedAnnualTurnover, growthTable.getAnnualTurnover());
        assertEquals(expectedResearchAndDevelopment, growthTable.getResearchAndDevelopment());
        assertEquals(expectedFinancialYearEnd, growthTable.getFinancialYearEnd());
        assertEquals(expectedEmployees, growthTable.getEmployees());
    }

    @Test
    public void updateFinanceDetails_ktpFinancialYears() {
        competition.setFundingType(FundingType.KTP);
        KtpFinancialYears ktpFinancialYears = new KtpFinancialYears();
        ktpFinancialYears.setYears(Lists.newArrayList(
                new KtpFinancialYear(0, ktpFinancialYears),
                new KtpFinancialYear(1, ktpFinancialYears),
                new KtpFinancialYear(2, ktpFinancialYears)
        ));
        when(finance.getKtpFinancialYears()).thenReturn(ktpFinancialYears);

        KtpYearsResource ktpYearsResource = new KtpYearsResource();
        ktpYearsResource.setYears(newKtpYearResource()
                        .withYear(0,1,2)
                        .withTurnover(BigDecimal.valueOf(1))
                        .withPreTaxProfit(BigDecimal.valueOf(2))
                        .withCurrentAssets(BigDecimal.valueOf(3))
                        .withLiabilities(BigDecimal.valueOf(4))
                        .withShareholderValue(BigDecimal.valueOf(5))
                        .withLoans(BigDecimal.valueOf(6))
                        .withEmployees(7L)
                        .build(3));
        ktpYearsResource.setFinancialYearEnd(LocalDate.now());
        ktpYearsResource.setGroupEmployees(1L);

        when(financeResource.getFinancialYearAccounts()).thenReturn(ktpYearsResource);

        service.updateFinancialYearData(finance, financeResource);

        assertEquals(ktpYearsResource.getGroupEmployees(), ktpFinancialYears.getGroupEmployees());
        assertEquals(ktpYearsResource.getFinancialYearEnd(), ktpFinancialYears.getFinancialYearEnd());

        KtpFinancialYear year0 = ktpFinancialYears.getYears().stream().filter(year -> year.getYear() == 0).findAny().get();
        assertEquals(ktpYearsResource.getYears().get(0).getTurnover(), year0.getTurnover());
        assertEquals(ktpYearsResource.getYears().get(0).getPreTaxProfit(), year0.getPreTaxProfit());
        assertEquals(ktpYearsResource.getYears().get(0).getCurrentAssets(), year0.getCurrentAssets());
        assertEquals(ktpYearsResource.getYears().get(0).getLiabilities(), year0.getLiabilities());
        assertEquals(ktpYearsResource.getYears().get(0).getLoans(), year0.getLoans());
        assertEquals(ktpYearsResource.getYears().get(0).getEmployees(), year0.getEmployees());

        KtpFinancialYear year1 = ktpFinancialYears.getYears().stream().filter(year -> year.getYear() == 1).findAny().get();
        assertEquals(ktpYearsResource.getYears().get(1).getTurnover(), year1.getTurnover());
        assertEquals(ktpYearsResource.getYears().get(1).getPreTaxProfit(), year1.getPreTaxProfit());
        assertEquals(ktpYearsResource.getYears().get(1).getCurrentAssets(), year1.getCurrentAssets());
        assertEquals(ktpYearsResource.getYears().get(1).getLiabilities(), year1.getLiabilities());
        assertEquals(ktpYearsResource.getYears().get(1).getLoans(), year1.getLoans());
        assertEquals(ktpYearsResource.getYears().get(1).getEmployees(), year1.getEmployees());

        KtpFinancialYear year2 = ktpFinancialYears.getYears().stream().filter(year -> year.getYear() == 2).findAny().get();
        assertEquals(ktpYearsResource.getYears().get(2).getTurnover(), year2.getTurnover());
        assertEquals(ktpYearsResource.getYears().get(2).getPreTaxProfit(), year2.getPreTaxProfit());
        assertEquals(ktpYearsResource.getYears().get(2).getCurrentAssets(), year2.getCurrentAssets());
        assertEquals(ktpYearsResource.getYears().get(2).getLiabilities(), year2.getLiabilities());
        assertEquals(ktpYearsResource.getYears().get(2).getLoans(), year2.getLoans());
        assertEquals(ktpYearsResource.getYears().get(2).getEmployees(), year2.getEmployees());
    }
}
