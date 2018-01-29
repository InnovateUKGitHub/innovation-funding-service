package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.domain.CostTotal;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.finance.builder.CostTotalBuilder.newCostTotal;

public class CostTotalBuilderTest {

    private Long[] ids = {1L, 2L};
    private Long[] financeIds = {10L, 11L};
    private String[] names = {FinanceRowType.MATERIALS.getName(), FinanceRowType.LABOUR.getName()};
    private String[] types = {"APPLICATION", "APPLICATION"};
    private BigDecimal[] totals = {BigDecimal.valueOf(10000L), BigDecimal.valueOf(20000L)};

    @Test
    public void buildOne() {
        CostTotal costTotal = newCostTotal()
                .withId(ids[0])
                .withFinanceId(financeIds[0])
                .withName(names[0])
                .withType(types[0])
                .withTotal(totals[0])
                .build();

        assertThat(costTotal.getId()).isEqualTo(ids[0]);
        assertThat(costTotal.getFinanceId()).isEqualTo(financeIds[0]);
        assertThat(costTotal.getName()).isEqualTo(names[0]);
        assertThat(costTotal.getType()).isEqualTo(types[0]);
        assertThat(costTotal.getTotal()).isEqualTo(totals[0]);
    }

    @Test
    public void buildMany() {
        List<CostTotal> costTotals = newCostTotal()
                .withId(ids[0], ids[1])
                .withFinanceId(financeIds[0], financeIds[1])
                .withName(names[0], names[1])
                .withType(types[0], types[1])
                .withTotal(totals[0], totals[1])
                .build(2);

        assertThat(costTotals)
                .extracting(CostTotal::getId)
                .containsExactly(ids[0], ids[1]);
        assertThat(costTotals)
                .extracting(CostTotal::getFinanceId)
                .containsExactly(financeIds[0], financeIds[1]);
        assertThat(costTotals)
                .extracting(CostTotal::getName)
                .containsExactly(names[0], names[1]);
        assertThat(costTotals)
                .extracting(CostTotal::getType)
                .containsExactly(types[0], types[1]);
        assertThat(costTotals)
                .extracting(CostTotal::getTotal)
                .containsExactly(totals[0], totals[1]);
    }
}
