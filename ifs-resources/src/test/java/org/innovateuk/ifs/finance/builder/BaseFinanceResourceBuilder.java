package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.finance.builder.AcademicCostBuilder.newAcademicCost;
import static org.innovateuk.ifs.finance.builder.CapitalUsageBuilder.newCapitalUsage;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OverheadBuilder.newOverhead;
import static org.innovateuk.ifs.finance.builder.OverheadCostCategoryBuilder.newOverheadCostCategory;
import static org.innovateuk.ifs.finance.builder.SubcontractingCostBuilder.newSubContractingCost;
import static org.innovateuk.ifs.finance.builder.TravelCostBuilder.newTravelCost;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * Base class Builder for building BaseFinanceResource entities.  This class holds build steps that are common to all
 * BaseFinanceResource subclasses.
 */
public abstract class BaseFinanceResourceBuilder<FinanceResourceType extends BaseFinanceResource, S extends BaseFinanceResourceBuilder<FinanceResourceType, S>>
        extends BaseBuilder<FinanceResourceType, S> {

    public S withOrganisation(Long... organisationIds) {
        return withArray((organisationId, applicationFinanceResource) -> setField("organisation", organisationId, applicationFinanceResource), organisationIds);
    }

    public S withOrganisationSize(OrganisationSize... value) {
        return withArray((v, finance) -> finance.setOrganisationSize(v), value);
    }

    public S withWorkPostcode(String... value) {
        return withArray((v, finance) -> finance.setWorkPostcode(v), value);
    }

    public S withFinanceOrganisationDetails(Map<FinanceRowType, FinanceRowCostCategory>... financeOrganisationDetails) {
        return withArray((financeOrganisationDetail, finance) -> setField("financeOrganisationDetails", financeOrganisationDetail, finance), financeOrganisationDetails);
    }

    public S withGrantClaimPercentage(Integer percentage) {
        return with(finance -> {
            GrantClaimCategory costCategory = new GrantClaimCategory();
            costCategory.addCost(new GrantClaim(null, percentage));
            costCategory.calculateTotal();
            finance.getFinanceOrganisationDetails().put(FINANCE, costCategory);
        });
    }

    protected BaseFinanceResourceBuilder(List<BiConsumer<Integer, FinanceResourceType>> newActions) {
        super(newActions);
    }

    public S withIndustrialCosts() {
        return withFinanceOrganisationDetails(asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withId(1L, 2L).
                                withGrossEmployeeCost(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
                                withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 120, 250).
                                build(3)).
                        build(),
                FinanceRowType.OVERHEADS, newOverheadCostCategory().withCosts(
                        newOverhead().
                                withId(1L).
                                withRateType(OverheadRateType.TOTAL).
                                withRate(1000).
                                build(1)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withId(1L, 2L).
                                withCost(new BigDecimal("33.33"), new BigDecimal("98.51")).
                                withQuantity(1, 2).
                                build(2)).
                        build(),
                FinanceRowType.CAPITAL_USAGE, newDefaultCostCategory().withCosts(
                        newCapitalUsage().
                                withId(1L, 2L).
                                withNpv(new BigDecimal("30"), new BigDecimal("70")).
                                withResidualValue(new BigDecimal("10"), new BigDecimal("35")).
                                withDeprecation(12, 20).
                                withUtilisation(80, 70).
                                withExisting("New", "Existing").
                                build(2)).
                        build(),
                FinanceRowType.SUBCONTRACTING_COSTS, newDefaultCostCategory().withCosts(
                        newSubContractingCost().
                                withId(1L, 2L).
                                withName("Bob", "Jim").
                                withCountry("UK", "Sweden").
                                withRole("Developer", "BA").
                                withCost(new BigDecimal("5000"), new BigDecimal("3000")).
                                build(2)).
                        build(),
                FinanceRowType.TRAVEL, newDefaultCostCategory().withCosts(
                        newTravelCost().
                                withId(1L, 2L).
                                withCost(new BigDecimal("30"), new BigDecimal("50")).
                                withItem("Train", "Bus").
                                withQuantity(20, 30).
                                build(2))
                        .build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(
                        newOtherCost().
                                withId(1L, 2L).
                                withDescription("Something", "Else").
                                withCost(new BigDecimal("100"), new BigDecimal("300")).
                                build(2))
                        .build()));
    }

    public S withAcademicCosts() {
        return withFinanceOrganisationDetails(asMap(
                FinanceRowType.YOUR_FINANCE, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withName("tsb_reference").
                                withItem("TSBReference").
                                withCostType(FinanceRowType.YOUR_FINANCE).
                                build(1)).
                        build(),
                FinanceRowType.LABOUR, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withName("incurred_staff", "allocated_investigators", "exceptions_staff").
                                withCost(new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300")).
                                withCostType(FinanceRowType.LABOUR, FinanceRowType.LABOUR, FinanceRowType.LABOUR).
                                build(3)).
                        build(),
                FinanceRowType.OVERHEADS, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withName("indirect_costs").
                                withCost(new BigDecimal("100")).
                                withCostType(FinanceRowType.OVERHEADS).
                                build(1)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withName("incurred_other_costs").
                                withCost(new BigDecimal("100")).
                                withCostType(FinanceRowType.MATERIALS).
                                build(1)).
                        build(),
                FinanceRowType.TRAVEL, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withName("incurred_travel_subsistence").
                                withCost(new BigDecimal("100")).
                                withCostType(FinanceRowType.MATERIALS).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(
                        newAcademicCost().
                                withName("allocated_estates_costs", "allocated_other_costs", "exceptions_other_costs").
                                withCost(new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("300")).
                                withCostType(FinanceRowType.OTHER_COSTS, FinanceRowType.OTHER_COSTS, FinanceRowType.OTHER_COSTS).
                                build(3))
                        .build()));
    }

}
