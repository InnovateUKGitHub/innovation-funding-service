package org.innovateuk.ifs.project.gol;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 **/
@RunWith(MockitoJUnitRunner.class)
public class YearlyGOLProfileTableTest {

    private static final String ROW_NAME_3 = "test1";
    private static final String ROW_NAME_4 = "test2";


    private static final BigDecimal CELL_1_1 = new BigDecimal("10.19");
    private static final BigDecimal CELL_1_2 = new BigDecimal("15.49");
    private static final BigDecimal CELL_2_1 = new BigDecimal("40.12");
    private static final BigDecimal CELL_2_2 = new BigDecimal("21.32");


    private static final Map<String, List<BigDecimal>> TABLE_DATA1 = ImmutableMap.<String, List<BigDecimal>>builder()
            .put(ROW_NAME_3, Lists.newArrayList(CELL_1_1, CELL_1_2))
            .put(ROW_NAME_4, Lists.newArrayList(CELL_2_1, CELL_2_2))
            .build();

    @Test
    public void getEligibleCostGrandTotalPerOrganisation() throws Exception {

        YearlyGOLProfileTable yearlyGOLProfileTable = new YearlyGOLProfileTable(null, null, TABLE_DATA1, null, null, null, null);

        Map<String, BigDecimal> eligibleCostGrandTotalPerOrganisation = yearlyGOLProfileTable.getEligibleCostGrandTotalPerOrganisation();

        assertEquals(eligibleCostGrandTotalPerOrganisation.get("test1"), BigDecimal.valueOf(25.68));
        assertEquals(eligibleCostGrandTotalPerOrganisation.get("test2"), BigDecimal.valueOf(61.44));

    }

    @Test
    public void getGrantAllocationGrandTotalPerOrganisation() throws Exception {

        YearlyGOLProfileTable yearlyGOLProfileTable = new YearlyGOLProfileTable(null, null, null, TABLE_DATA1, null, null, null);

        Map<String, BigDecimal> grandTotalPerOrganisation = yearlyGOLProfileTable.getGrantAllocationGrandTotalPerOrganisation();

        assertEquals(grandTotalPerOrganisation.get("test1"), BigDecimal.valueOf(25.68));
        assertEquals(grandTotalPerOrganisation.get("test2"), BigDecimal.valueOf(61.44));

    }

}