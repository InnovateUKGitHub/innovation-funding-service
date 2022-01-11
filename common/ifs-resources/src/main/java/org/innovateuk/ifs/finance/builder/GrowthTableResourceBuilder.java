package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.GrowthTableResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for EmployeesAndTurnoverResource entities.
 */
public class GrowthTableResourceBuilder extends BaseBuilder<GrowthTableResource, GrowthTableResourceBuilder> {

    public GrowthTableResourceBuilder withFinancialYearEnd(LocalDate... financialYearEnds) {
        return withArray((financialYearEnd, growthTableResource) -> growthTableResource.setFinancialYearEnd(financialYearEnd), financialYearEnds);
    }

    public GrowthTableResourceBuilder withAnnualTurnovers(BigDecimal... annualTurnovers) {
        return withArray((annualTurnover, growthTableResource) -> growthTableResource.setAnnualTurnover(annualTurnover), annualTurnovers);
    }

    public GrowthTableResourceBuilder withAnnualProfits(BigDecimal... annualProfits) {
        return withArray((annualProfit, growthTableResource) -> growthTableResource.setAnnualProfits(annualProfit), annualProfits);
    }

    public GrowthTableResourceBuilder withAnnualExport(BigDecimal... annualExports) {
        return withArray((annualExport, growthTableResource) -> growthTableResource.setAnnualExport(annualExport), annualExports);
    }

    public GrowthTableResourceBuilder withResearchAndDevelopment(BigDecimal... researchAndDevelopments) {
        return withArray((researchAndDevelopment, growthTableResource) -> growthTableResource.setResearchAndDevelopment(researchAndDevelopment), researchAndDevelopments);
    }

    public GrowthTableResourceBuilder withEmployees(Long... employees) {
        return withArray((employee, growthTableResource) -> growthTableResource.setEmployees(employee), employees);
    }

    private GrowthTableResourceBuilder(List<BiConsumer<Integer, GrowthTableResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static GrowthTableResourceBuilder newGrowthTableResource() {
        return new GrowthTableResourceBuilder(emptyList());
    }

    @Override
    protected GrowthTableResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrowthTableResource>> actions) {
        return new GrowthTableResourceBuilder(actions);
    }

    @Override
    protected GrowthTableResource createInitial() {
        return new GrowthTableResource();
    }
}