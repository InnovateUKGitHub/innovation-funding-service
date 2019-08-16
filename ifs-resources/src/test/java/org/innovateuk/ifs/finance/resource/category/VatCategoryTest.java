package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.builder.VATCategoryBuilder.newVATCategory;
import static org.innovateuk.ifs.finance.builder.VATCostBuilder.newVATCost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VatCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private VatCostCategory vatCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem vat = newVATCost().withRegistered(false).build();
        FinanceRowItem vat2 = newVATCost().withRegistered(false).build();

        costs.add(vat);
        costs.add(vat2);

        vatCategory = newVATCategory().withCosts(asList(vat, vat2)).build();
    }

    @Test
    public void getCosts() {
        assertEquals(costs, vatCategory.getCosts());
    }

    @Test
    public void getTotal() {
        vatCategory.calculateTotal();
        assertEquals(new BigDecimal(0), vatCategory.getTotal());
    }

    @Test
    public void addCost() {
        FinanceRowItem vat3 = newVATCost().withRegistered(false).build();
        costs.add(vat3);
        vatCategory.addCost(vat3);

        assertEquals(costs, vatCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {
        assertTrue(vatCategory.excludeFromTotalCost());
    }
}
