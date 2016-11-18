package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.Materials;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class MaterialsTest {
    private Long id;
    private String item;
    private BigDecimal cost;
    private Integer quantity;
    private Materials materials;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        item = "Valves";
        cost = new BigDecimal(2000);
        quantity = 12;
        materials = new Materials(id, item, cost, quantity);
    }

    @Test
    public void materialsShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(materials.getId().equals(id));
        assert(materials.getItem().equals(item));
        assert(materials.getCost().equals(cost));
        assert(materials.getQuantity().equals(quantity));
    }

    @Test
    public void calculateTotalsForMaterialsTest() throws Exception {
        BigDecimal expected = new BigDecimal(2000).multiply(new BigDecimal(12));
        assertEquals(expected, materials.getTotal());
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        Materials materialWithoutValues = new Materials();
        assertEquals(BigDecimal.ZERO, materialWithoutValues.getTotal());
    }
}
