package org.innovateuk.ifs.finance.builder.sync;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class FinanceCostTotalResourceBuilder extends BaseBuilder<FinanceCostTotalResource, FinanceCostTotalResourceBuilder> {
    protected FinanceCostTotalResourceBuilder(List<BiConsumer<Integer, FinanceCostTotalResource>> multiActions) {
        super(multiActions);
    }

    @Override
    protected FinanceCostTotalResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCostTotalResource>> actions) {
        return new FinanceCostTotalResourceBuilder(actions);
    }

    @Override
    protected FinanceCostTotalResource createInitial() {
        return new FinanceCostTotalResource();
    }

    public static FinanceCostTotalResourceBuilder newFinanceCostTotalResource() {
        return new FinanceCostTotalResourceBuilder(emptyList());
    }

    public FinanceCostTotalResourceBuilder withTotal(BigDecimal... costTotal) {
        return withArraySetFieldByReflection("total", costTotal);
    }

    public FinanceCostTotalResourceBuilder withFinanceType(FinanceType... financeTypes) {
        return withArraySetFieldByReflection("financeType", financeTypes);
    }

    public FinanceCostTotalResourceBuilder withFinanceRowType(FinanceRowType... rowTypes) {
        return withArraySetFieldByReflection("financeRowType", rowTypes);
    }

    public FinanceCostTotalResourceBuilder withFinanceId(Long... financeId) {
        return withArraySetFieldByReflection("financeId", financeId);
    }
}
