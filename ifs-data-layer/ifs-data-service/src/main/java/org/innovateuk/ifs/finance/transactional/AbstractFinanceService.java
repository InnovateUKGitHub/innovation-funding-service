package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.EmployeesAndTurnoverResource;
import org.innovateuk.ifs.finance.resource.GrowthTableResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;

import static java.lang.Boolean.TRUE;

public abstract class AbstractFinanceService<D extends Finance, F extends BaseFinanceResource> extends BaseTransactionalService {

    protected void updateFinanceDetails(D dbFinance, F financeResource) {
        if (financeResource.getOrganisationSize() != null) {
            dbFinance.setOrganisationSize(financeResource.getOrganisationSize());
        }
        if (dbFinance.getOrganisationSize() != financeResource.getOrganisationSize()) {
            dbFinance.setOrganisationSize(financeResource.getOrganisationSize());
        }
        if (TRUE.equals(dbFinance.getCompetition().getIncludeProjectGrowthTable())) {
            updateGrowthTable(financeResource, dbFinance);
        } else {
            updateEmployeesAndTurnover(financeResource, dbFinance);
        }
    }

    private void updateEmployeesAndTurnover(F financeResource, D dbFinance) {
        EmployeesAndTurnover employeesAndTurnover = dbFinance.getEmployeesAndTurnover();
        EmployeesAndTurnoverResource employeesAndTurnoverResource = (EmployeesAndTurnoverResource) financeResource.getFinancialYearAccounts();
        employeesAndTurnover.setTurnover(employeesAndTurnoverResource.getTurnover());
        employeesAndTurnover.setEmployees(employeesAndTurnoverResource.getEmployees());
    }

    private void updateGrowthTable(F financeResource, D dbFinance) {
        GrowthTable growthTable = dbFinance.getGrowthTable();
        GrowthTableResource growthTableResource = (GrowthTableResource) financeResource.getFinancialYearAccounts();
        growthTable.setAnnualExport(growthTableResource.getAnnualExport());
        growthTable.setAnnualProfits(growthTableResource.getAnnualProfits());
        growthTable.setAnnualTurnover(growthTableResource.getAnnualTurnover());
        growthTable.setResearchAndDevelopment(growthTableResource.getResearchAndDevelopment());
        growthTable.setFinancialYearEnd(growthTableResource.getFinancialYearEnd());
        growthTable.setEmployees(growthTableResource.getEmployees());
    }
}