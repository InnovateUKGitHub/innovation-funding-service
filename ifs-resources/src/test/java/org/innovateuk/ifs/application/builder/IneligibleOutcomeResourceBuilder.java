package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class IneligibleOutcomeResourceBuilder
        extends BaseBuilder<IneligibleOutcomeResource, IneligibleOutcomeResourceBuilder> {

    private IneligibleOutcomeResourceBuilder(List<BiConsumer<Integer,
            IneligibleOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static IneligibleOutcomeResourceBuilder newIneligibleOutcomeResource() {
        return new IneligibleOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected IneligibleOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            IneligibleOutcomeResource>> actions) {
        return new IneligibleOutcomeResourceBuilder(actions);
    }

    @Override
    protected IneligibleOutcomeResource createInitial() {
        return new IneligibleOutcomeResource();
    }

    public IneligibleOutcomeResourceBuilder withReason(String... reasons) {
        return withArray((reason, ineligibleOutcomeResource) -> ineligibleOutcomeResource.setReason(reason), reasons);
    }

    public IneligibleOutcomeResourceBuilder withRemovedBy(String... removedBys) {
        return withArray((removedBy, ineligibleOutcomeResource) -> ineligibleOutcomeResource.setRemovedBy(removedBy), removedBys);
    }

    public IneligibleOutcomeResourceBuilder withRemovedOn(ZonedDateTime... removedOns) {
        return withArray((removedOn, ineligibleOutcomeResource) -> ineligibleOutcomeResource.setRemovedOn(removedOn), removedOns);
    }
}
