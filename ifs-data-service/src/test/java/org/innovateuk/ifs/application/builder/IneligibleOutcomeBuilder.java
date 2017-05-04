package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class IneligibleOutcomeBuilder extends BaseBuilder<IneligibleOutcome, IneligibleOutcomeBuilder> {

    private IneligibleOutcomeBuilder(List<BiConsumer<Integer, IneligibleOutcome>> multiActions) {
        super(multiActions);
    }

    public static IneligibleOutcomeBuilder newIneligibleOutcome() {
        return new IneligibleOutcomeBuilder(emptyList());
    }

    @Override
    protected IneligibleOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, IneligibleOutcome>> actions) {
        return new IneligibleOutcomeBuilder(actions);
    }

    @Override
    protected IneligibleOutcome createInitial() {
        return new IneligibleOutcome();
    }

    public IneligibleOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public IneligibleOutcomeBuilder withReason(String... reasons) {
        return withArray((reason, ineligibleOutcome) -> ineligibleOutcome.setReason(reason), reasons);
    }
}
