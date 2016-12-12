package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public ProcessOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public ProcessOutcomeBuilder withOutcome(String... outcomes) {
        return withArray((outcome, processOutcome) -> setField("outcome", outcome, processOutcome), outcomes);
    }

    public ProcessOutcomeBuilder withOutcomeType(String... outcomeTypes) {
        return withArray((outcomeType, processOutcome) -> setField("outcomeType", outcomeType, processOutcome), outcomeTypes);
    }

    public ProcessOutcomeBuilder withDescription(String... descriptions) {
        return withArray((description, processOutcome) -> setField("description", description, processOutcome), descriptions);
    }

    public ProcessOutcomeBuilder withComment(String... comments) {
        return withArray((comment, processOutcome) -> setField("comment", comment, processOutcome), comments);
    }

}
