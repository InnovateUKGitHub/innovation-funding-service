package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class EquipmentTest {
    private Long id;
    private String item;
    private BigDecimal cost;
    private Integer quantity;
    private Equipment equipment;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        item = "Valves";
        cost = new BigDecimal(3200);
        quantity = 5;
        equipment = new Equipment(id, item, cost, quantity, 1L);
    }

    @Test
    public void materialsShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(equipment.getId().equals(id));
        assert(equipment.getItem().equals(item));
        assert(equipment.getCost().equals(cost));
        assert(equipment.getQuantity().equals(quantity));
    }

    @Test
    public void calculateTotalsForMaterialsTest() throws Exception {
        BigDecimal expected = new BigDecimal(3200).multiply(new BigDecimal(5));
        assertEquals(expected, equipment.getTotal());
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        Equipment materialWithoutValues = new Equipment(1L);
        assertEquals(BigDecimal.ZERO, materialWithoutValues.getTotal());
    }
}
