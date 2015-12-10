package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class ProcessOutcomeBuilder extends BaseBuilder<ProcessOutcome, ProcessOutcomeBuilder> {
    private ProcessOutcomeBuilder(List<BiConsumer<Integer, ProcessOutcome>> multiActions) {
        super(multiActions);
    }

    public static ProcessOutcomeBuilder newProcessOutcome() {
        return new ProcessOutcomeBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProcessOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProcessOutcome>> actions) {
        return new ProcessOutcomeBuilder(actions);
    }

    @Override
    protected ProcessOutcome createInitial() {
        return new ProcessOutcome();
    }
}
