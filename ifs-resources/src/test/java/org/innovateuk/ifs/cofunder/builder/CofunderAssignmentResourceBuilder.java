package org.innovateuk.ifs.cofunder.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CofunderAssignmentResourceBuilder extends BaseBuilder<CofunderAssignmentResource, CofunderAssignmentResourceBuilder> {

    private CofunderAssignmentResourceBuilder(List<BiConsumer<Integer, CofunderAssignmentResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CofunderAssignmentResourceBuilder newCofunderAssignmentResource() {
        return new CofunderAssignmentResourceBuilder(emptyList());
    }

    @Override
    protected CofunderAssignmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CofunderAssignmentResource>> actions) {
        return new CofunderAssignmentResourceBuilder(actions);
    }

    @Override
    protected CofunderAssignmentResource createInitial() {
        return new CofunderAssignmentResource();
    }

    public CofunderAssignmentResourceBuilder withAssignmentId(long... assignmentIds) {
        return withArray((id, assignment) -> assignment.setAssignmentId(id), assignmentIds);
    }

    public CofunderAssignmentResourceBuilder withState(CofunderState... cofunderStates) {
        return withArray((cofunderState, assignment) -> assignment.setState(cofunderState), cofunderStates);
    }

    public CofunderAssignmentResourceBuilder withComments(String... comments) {
        return withArray((comment, assignment) -> assignment.setComments(comment), comments);
    }
}