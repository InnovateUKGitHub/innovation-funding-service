package com.worth.ifs.project.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class FinanceCheckSummaryResourceBuilder extends BaseBuilder<FinanceCheckSummaryResource, FinanceCheckSummaryResourceBuilder> {

    private FinanceCheckSummaryResourceBuilder(List<BiConsumer<Integer, FinanceCheckSummaryResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckSummaryResourceBuilder newFinanceCheckSummaryResource() {
        return new FinanceCheckSummaryResourceBuilder(emptyList());
    }

    @Override
    protected FinanceCheckSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckSummaryResource>> actions) {
        return new FinanceCheckSummaryResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckSummaryResource createInitial() {
        return new FinanceCheckSummaryResource();
    }

    public FinanceCheckSummaryResourceBuilder withProjectId(Long... projectIds) {
        return withArray((projectId, financeCheckResource) -> setField("projectId", projectId, financeCheckResource), projectIds);
    }

    public FinanceCheckSummaryResourceBuilder withCompetitionId(CostGroupResource... competitionIds) {
        return withArray((costGroup, financeCheckResource) -> setField("competitionId", costGroup, financeCheckResource), competitionIds);
    }

}
