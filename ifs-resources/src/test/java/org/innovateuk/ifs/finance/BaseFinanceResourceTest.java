package org.innovateuk.ifs.finance;

import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.resource.cost.Vat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.VATCategoryBuilder.newVATCategory;
import static org.innovateuk.ifs.finance.builder.VATCostBuilder.newVATCost;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

public class BaseFinanceResourceTest {

    private BaseFinanceResource baseFinanceResource;

    @Before
    public void setUp() throws Exception {
        baseFinanceResource = Mockito.mock(BaseFinanceResource.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getGrantClaim() {

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(30).
                                build(1)).
                        build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        GrantClaimPercentage grantClaim = (GrantClaimPercentage) baseFinanceResource.getGrantClaim();

        assertEquals(grantClaim.getPercentage(), Integer.valueOf(30));
    }

    @Test
    public void getGrantClaimPercentage() {
        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(30).
                                build(1)).
                        withTotal(BigDecimal.valueOf(30)).
                        build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        Integer grantClaimPercentage = baseFinanceResource.getGrantClaimPercentage();

        assertEquals(grantClaimPercentage, Integer.valueOf(30));
    }

    @Test
    public void getTotalFundingSought() {
        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("10000000"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 200).
                                withTotal(BigDecimal.valueOf(100)).
                                build(2)).
                        withTotal(BigDecimal.valueOf(100)).
                        build(),
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(50).
                                build(1)).
                        withTotal(BigDecimal.valueOf(50)).
                        build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        assertEquals(baseFinanceResource.getTotalFundingSought(), BigDecimal.valueOf(50));
    }

    @Test
    public void getTotalContribution() {

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("10000000"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 200).
                                withTotal(BigDecimal.valueOf(100)).
                                build(2)).
                        withTotal(BigDecimal.valueOf(100)).
                        build(),
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(50).
                                build(1)).
                        withTotal(BigDecimal.valueOf(50)).
                        build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        assertEquals(baseFinanceResource.getTotalContribution(), BigDecimal.valueOf(50));
    }

    @Test
    public void getTotal_WithoutVAT() {

        Vat vat = newVATCost()
                .withRegistered(false)
                .withRate(new BigDecimal("0.2"))
                .build();

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("10000000"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 200).
                                withTotal(BigDecimal.valueOf(100)).
                                build(2)).
                        withTotal(BigDecimal.valueOf(100)).
                        build(),
                FinanceRowType.VAT,  newVATCategory().withCosts(singletonList(vat)).build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        BigDecimal totalCost = baseFinanceResource.getTotal();
        assertEquals(totalCost, BigDecimal.valueOf(100));
    }

    @Test
    public void getTotal_withVAT() {

        Vat vat = newVATCost()
                .withRegistered(true)
                .withRate(new BigDecimal("0.2"))
                .build();

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("10000000"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 200).
                                withTotal(BigDecimal.valueOf(100)).
                                build(2)).
                        withTotal(BigDecimal.valueOf(100)).
                        build(),
                FinanceRowType.VAT,  newVATCategory().withCosts(singletonList(vat)).withTotal(new BigDecimal("20")).build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        BigDecimal totalCost = baseFinanceResource.getTotal();
        assertEquals(totalCost, new BigDecimal("120"));
    }

    @Test
    public void isVatRegistered_null() {

        Vat vat = newVATCost()
                .build();

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.VAT,  newVATCategory().withCosts(singletonList(vat))
                        .build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        assertFalse(baseFinanceResource.isVatRegistered());
    }

    @Test
    public void isVatRegistered_false() {

        Vat vat = newVATCost()
                .withRegistered(false)
                .build();

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.VAT,  newVATCategory().withCosts(singletonList(vat))
                        .build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        assertFalse(baseFinanceResource.isVatRegistered());
    }

    @Test
    public void isVatRegistered_true() {

        Vat vat = newVATCost()
                .withRegistered(true)
                .build();

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
                FinanceRowType.VAT,  newVATCategory().withCosts(singletonList(vat))
                        .build());

        baseFinanceResource.setFinanceOrganisationDetails(financeOrganisationDetails);

        assertTrue(baseFinanceResource.isVatRegistered());
    }
}
