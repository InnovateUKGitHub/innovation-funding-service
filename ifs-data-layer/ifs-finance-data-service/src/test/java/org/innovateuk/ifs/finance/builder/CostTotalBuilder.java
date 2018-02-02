package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.domain.CostTotal;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CostTotalBuilder extends BaseBuilder<CostTotal, CostTotalBuilder> {

    public CostTotalBuilder(List<BiConsumer<Integer, CostTotal>> newActions) {
        super(newActions);
    }

    @Override
    protected CostTotalBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostTotal>> actions) {
        return new CostTotalBuilder(actions);
    }

    @Override
    protected CostTotal createInitial() {
        return new CostTotal();
    }

    public static CostTotalBuilder newCostTotal() {
        return new CostTotalBuilder(emptyList());
    }

    public CostTotalBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public CostTotalBuilder withFinanceId(Long... financeId) {
        return withArraySetFieldByReflection("financeId", financeId);
    }

    public CostTotalBuilder withName(String... name) {
        return withArraySetFieldByReflection("name", name);
    }

    public CostTotalBuilder withType(String... type) {
        return withArraySetFieldByReflection("type", type);
    }

    public CostTotalBuilder withTotal(BigDecimal... total) {
        return withArraySetFieldByReflection("total", total);
    }
}
