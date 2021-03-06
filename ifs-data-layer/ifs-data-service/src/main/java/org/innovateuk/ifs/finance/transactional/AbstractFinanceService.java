package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.repository.EmployeesAndTurnoverRepository;
import org.innovateuk.ifs.finance.repository.GrowthTableRepository;
import org.innovateuk.ifs.finance.repository.KtpFinancialYearsRepository;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.EmployeesAndTurnoverResource;
import org.innovateuk.ifs.finance.resource.GrowthTableResource;
import org.innovateuk.ifs.finance.resource.KtpYearsResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.TRUE;

public abstract class AbstractFinanceService<D extends Finance, F extends BaseFinanceResource> extends BaseTransactionalService {
    @Autowired
    private EmployeesAndTurnoverRepository employeesAndTurnoverRepository;

    @Autowired
    private GrowthTableRepository growthTableRepository;

    @Autowired
    private KtpFinancialYearsRepository ktpFinancialYearsRepository;

    protected void initialiseFinancialYearData(D finance) {
        if (finance.getCompetition().getFundingType() == FundingType.KTP) {
            KtpFinancialYears ktpFinancialYears = new KtpFinancialYears();
            ktpFinancialYears.setYears(newArrayList(
                    new KtpFinancialYear(0, ktpFinancialYears),
                    new KtpFinancialYear(1, ktpFinancialYears),
                    new KtpFinancialYear(2, ktpFinancialYears)
            ));
            finance.setKtpFinancialYears(ktpFinancialYears);
            ktpFinancialYearsRepository.save(ktpFinancialYears);
        } else if (TRUE.equals(finance.getCompetition().getIncludeProjectGrowthTable())) {
            finance.setGrowthTable(new GrowthTable());
            growthTableRepository.save(finance.getGrowthTable());
        } else {
            finance.setEmployeesAndTurnover(new EmployeesAndTurnover());
            employeesAndTurnoverRepository.save(finance.getEmployeesAndTurnover());
        }
    }

    protected void updateFinancialYearData(D dbFinance, F financeResource) {
        if (financeResource.getOrganisationSize() != null) {
            dbFinance.setOrganisationSize(financeResource.getOrganisationSize());
        }
        if (dbFinance.getCompetition().getFundingType() == FundingType.KTP) {
            updateKtpYears(financeResource, dbFinance);
        } else if (TRUE.equals(dbFinance.getCompetition().getIncludeProjectGrowthTable())) {
            updateGrowthTable(financeResource, dbFinance);
        } else {
            updateEmployeesAndTurnover(financeResource, dbFinance);
        }
    }

    private void updateKtpYears(F financeResource, D dbFinance) {
        KtpFinancialYears dbYears = dbFinance.getKtpFinancialYears();
        KtpYearsResource ktpYearsResource = (KtpYearsResource) financeResource.getFinancialYearAccounts();
        ktpYearsResource.getYears().forEach(year -> {
            KtpFinancialYear dbYear = dbYears.getYears()
                    .stream()
                    .filter(filterYear -> filterYear.getYear().equals(year.getYear()))
                    .findFirst()
                    .orElseThrow(ObjectNotFoundException::new);
            dbYear.setTurnover(year.getTurnover());
            dbYear.setPreTaxProfit(year.getPreTaxProfit());
            dbYear.setCurrentAssets(year.getCurrentAssets());
            dbYear.setLiabilities(year.getLiabilities());
            dbYear.setShareholderValue(year.getShareholderValue());
            dbYear.setLoans(year.getLoans());
            dbYear.setEmployees(year.getEmployees());

        });
        dbYears.setGroupEmployees(ktpYearsResource.getGroupEmployees());
        dbYears.setFinancialYearEnd(ktpYearsResource.getFinancialYearEnd());
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