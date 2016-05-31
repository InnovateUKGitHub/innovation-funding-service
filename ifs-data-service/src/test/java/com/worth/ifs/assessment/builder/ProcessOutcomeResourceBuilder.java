package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ProcessOutcomeResourceBuilder extends BaseBuilder<ProcessOutcomeResource, ProcessOutcomeResourceBuilder> {
    private ProcessOutcomeResourceBuilder(List<BiConsumer<Integer, ProcessOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static ProcessOutcomeResourceBuilder newProcessOutcomeResource() {
        return new ProcessOutcomeResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProcessOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcessOutcomeResource>> actions) {
        return new ProcessOutcomeResourceBuilder(actions);
    }

    @Override
    protected ProcessOutcomeResource createInitial() {
        return new ProcessOutcomeResource();
    }

    public ProcessOutcomeResourceBuilder withOutcome(String outcome) {
        return with(processOutcome -> processOutcome.setOutcome(outcome));
    }
}
