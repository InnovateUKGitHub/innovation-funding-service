package org.innovateuk.ifs.finance.builder;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;


import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.function.BiConsumer;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

public class OrganisationFinancesWithGrowthTableResourceBuilder extends BaseBuilder<OrganisationFinancesWithGrowthTableResource, OrganisationFinancesWithGrowthTableResourceBuilder> {

    protected OrganisationFinancesWithGrowthTableResourceBuilder() {
        super();
    }

    protected OrganisationFinancesWithGrowthTableResourceBuilder(List<BiConsumer<Integer, OrganisationFinancesWithGrowthTableResource>> newActions) {
        super(newActions);
    }

    public static OrganisationFinancesWithGrowthTableResourceBuilder newOrganisationFinancesWithGrowthTableResource() {
        return new OrganisationFinancesWithGrowthTableResourceBuilder();
    }
    
    @Override
    protected OrganisationFinancesWithGrowthTableResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
        OrganisationFinancesWithGrowthTableResource>> actions) {
        return new OrganisationFinancesWithGrowthTableResourceBuilder(actions);
    }

    @Override
    protected OrganisationFinancesWithGrowthTableResource createInitial() {
        return createDefault(OrganisationFinancesWithGrowthTableResource.class);
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withOrganisationSize(OrganisationSize organisationSize) {
        return with(organisationFinancesWithGrowthTableResource -> setField("organisationSize", organisationSize,
            organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withHeadCount(Long headCountAtLastFinancialYear) {
        return with(organisationFinancesWithGrowthTableResource -> setField("headCountAtLastFinancialYear", headCountAtLastFinancialYear,
            organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withStateAidAgreed(boolean stateAidAgreed) {
        return with(organisationFinancesWithGrowthTableResource -> setField("stateAidAgreed", stateAidAgreed,
            organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withTurnover(BigDecimal annualTurnoverAtLastFinancialYear) {
        return with(organisationFinancesWithGrowthTableResource -> setField("annualTurnoverAtLastFinancialYear",
            annualTurnoverAtLastFinancialYear, organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withAnnualProfits(BigDecimal annualProfitsAtLastFinancialYear) {
        return with(organisationFinancesWithGrowthTableResource -> setField("annualProfitsAtLastFinancialYear",
            annualProfitsAtLastFinancialYear, organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withAnnualExport(BigDecimal annualExportAtLastFinancialYear) {
        return with(organisationFinancesWithGrowthTableResource -> setField("annualExportAtLastFinancialYear",
            annualExportAtLastFinancialYear, organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withResearchAndDevelopment(BigDecimal researchAndDevelopmentSpendAtLastFinancialYear) {
        return with(organisationFinancesWithGrowthTableResource -> setField("researchAndDevelopmentSpendAtLastFinancialYear",
            researchAndDevelopmentSpendAtLastFinancialYear, organisationFinancesWithGrowthTableResource));
    }

    public OrganisationFinancesWithGrowthTableResourceBuilder withFinancialYearEnd(YearMonth financialYearEnd) {
        return with(organisationFinancesWithGrowthTableResource -> setField("financialYearEnd", financialYearEnd,
            organisationFinancesWithGrowthTableResource));
    }
}
