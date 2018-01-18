package org.innovateuk.ifs.finance.builder.sync;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * TODO: Add description
 */
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
}
