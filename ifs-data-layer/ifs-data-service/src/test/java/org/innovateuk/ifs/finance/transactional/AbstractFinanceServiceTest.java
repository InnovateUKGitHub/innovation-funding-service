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
}
