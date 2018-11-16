package org.innovateuk.ifs.application.forms.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
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
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@RunWith(MockitoJUnitRunner.class)
public class AbstractYourProjectCostsFormPopulatorTest {

    @InjectMocks
    private AbstractYourProjectCostsFormPopulator target = new AbstractYourProjectCostsFormPopulator() {
        @Override
        protected BaseFinanceResource getFinanceResource(long targetId, long organisationId) {
            Map<FinanceRowType, FinanceRowCostCategory> industrialOrganisationFinances = asMap(
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
                                    withRateType(OverheadRateType.CUSTOM_RATE).
                                    withCalculationFile(Optional.of(newFileEntryResource().withName("filename").build())).
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
                            .build());


            return newApplicationFinanceResource().withFinanceOrganisationDetails(industrialOrganisationFinances).build();
        }

        @Override
        protected boolean shouldAddEmptyRow() {
            return true;
        }
    };

    @Test
    public void populate() {
        YourProjectCostsForm form = new YourProjectCostsForm();

        target.populateForm(form, 1L, 2L);

        Assert.assertEquals((Integer) 250, form.getLabour().getWorkingDaysPerYear());
        Assert.assertEquals(3, form.getLabour().getRows().size());

        Assert.assertEquals((Long) 1L, form.getOverhead().getCostId());
        Assert.assertEquals(OverheadRateType.CUSTOM_RATE, form.getOverhead().getRateType());
        Assert.assertEquals("filename", form.getOverhead().getFilename());
        Assert.assertEquals((Integer) 1000, form.getOverhead().getTotalSpreadsheet());

        Assert.assertEquals(3, form.getMaterialRows().size());
        Assert.assertEquals(3, form.getCapitalUsageRows().size());
        Assert.assertEquals(3, form.getSubcontractingRows().size());
        Assert.assertEquals(3, form.getTravelRows().size());
        Assert.assertEquals(3, form.getOtherRows().size());
    }

}
