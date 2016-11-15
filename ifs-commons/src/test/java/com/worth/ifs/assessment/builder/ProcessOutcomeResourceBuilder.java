package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ProcessOutcomeResourceBuilder extends BaseBuilder<ProcessOutcomeResource, ProcessOutcomeResourceBuilder> {
    private ProcessOutcomeResourceBuilder(List<BiConsumer<Integer, ProcessOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static ProcessOutcomeResourceBuilder newProcessOutcomeResource() {
        return new ProcessOutcomeResourceBuilder(emptyList()).with(BaseBuilderAmendFunctions.uniqueIds());
    }

    @Override
    protected ProcessOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcessOutcomeResource>> actions) {
        return new ProcessOutcomeResourceBuilder(actions);
    }

    @Override
    protected ProcessOutcomeResource createInitial() {
        return new ProcessOutcomeResource();
    }

    public ProcessOutcomeResourceBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public ProcessOutcomeResourceBuilder withOutcome(String... outcomes) {
        return withArray((outcome, processOutcomeResource) -> BaseBuilderAmendFunctions.setField("outcome", outcome, processOutcomeResource), outcomes);
    }

    public ProcessOutcomeResourceBuilder withDescription(String... descriptions) {
        return withArray((description, processOutcomeResource) -> BaseBuilderAmendFunctions.setField("description", description, processOutcomeResource), descriptions);
    }

    public ProcessOutcomeResourceBuilder withComment(String... comments) {
        return withArray((comment, processOutcomeResource) -> BaseBuilderAmendFunctions.setField("comment", comment, processOutcomeResource), comments);
    }

    public ProcessOutcomeResourceBuilder withOutcomeType(String... outcomeTypes) {
        return withArray((outcomeType, processOutcomeResource) -> BaseBuilderAmendFunctions.setField("outcomeType", outcomeType, processOutcomeResource), outcomeTypes);
    }
}
