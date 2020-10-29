package org.innovateuk.ifs.supporter.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class SupporterAssignmentResourceBuilder extends BaseBuilder<SupporterAssignmentResource, SupporterAssignmentResourceBuilder> {

    private SupporterAssignmentResourceBuilder(List<BiConsumer<Integer, SupporterAssignmentResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static SupporterAssignmentResourceBuilder newSupporterAssignmentResource() {
        return new SupporterAssignmentResourceBuilder(emptyList());
    }

    @Override
    protected SupporterAssignmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SupporterAssignmentResource>> actions) {
        return new SupporterAssignmentResourceBuilder(actions);
    }

    @Override
    protected SupporterAssignmentResource createInitial() {
        return new SupporterAssignmentResource();
    }

    public SupporterAssignmentResourceBuilder withAssignmentId(long... assignmentIds) {
        return withArray((id, assignment) -> assignment.setAssignmentId(id), assignmentIds);
    }

    public SupporterAssignmentResourceBuilder withState(SupporterState... supporterStates) {
        return withArray((supporterState, assignment) -> assignment.setState(supporterState), supporterStates);
    }

    public SupporterAssignmentResourceBuilder withComments(String... comments) {
        return withArray((comment, assignment) -> assignment.setComments(comment), comments);
    }
}