package org.innovateuk.ifs.finance.resource.category;

import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.builder.VATCategoryBuilder.newVATCategory;
import static org.innovateuk.ifs.finance.builder.VATCostBuilder.newVATCost;
import static org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory.ZERO_COST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class VatCategoryTest {

    @Test
    public void getTotal_notRegistered() {
        VatCostCategory vatCategory = newVATCategory().withCosts(newVATCost().withRegistered(false).build(1)).build();
        vatCategory.setTotalCostsWithoutVat(new BigDecimal(100));
        vatCategory.calculateTotal();
        assertEquals(ZERO_COST, vatCategory.getTotal());
    }

    @Test
    public void getTotal_Registered() {
        VatCostCategory vatCategory = newVATCategory().withCosts(newVATCost().withRegistered(true).withRate(new BigDecimal("0.2")).build(1)).build();
        vatCategory.setTotalCostsWithoutVat(new BigDecimal(100));
        vatCategory.calculateTotal();
        assertEquals(new BigDecimal("20.00"), vatCategory.getTotal());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {
        assertFalse(newVATCategory().build().excludeFromTotalCost());
    }
}
