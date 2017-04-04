package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class FinanceCheckProcessResourceBuilder extends BaseBuilder<FinanceCheckProcessResource, FinanceCheckProcessResourceBuilder> {

    private FinanceCheckProcessResourceBuilder(List<BiConsumer<Integer, FinanceCheckProcessResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckProcessResourceBuilder newFinanceCheckProcessResource() {
        return new FinanceCheckProcessResourceBuilder(emptyList());
    }

    @Override
    protected FinanceCheckProcessResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckProcessResource>> actions) {
        return new FinanceCheckProcessResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckProcessResource createInitial() {
        return newInstance(FinanceCheckProcessResource.class);
    }

    public FinanceCheckProcessResourceBuilder withCanApprove(Boolean... approval) {
        return withArray((flag, financeCheckResource) -> financeCheckResource.setCanApprove(flag), approval);
    }

    public FinanceCheckProcessResourceBuilder withState(FinanceCheckState... state) {
        return withArray((s, financeCheckResource) -> financeCheckResource.setCurrentState(s), state);
    }

    public FinanceCheckProcessResourceBuilder withParticipant(ProjectUserResource... participant) {
        return withArray((p, financeCheckResource) -> financeCheckResource.setParticipant(p), participant);
    }

    public FinanceCheckProcessResourceBuilder withInternalParticipant(UserResource... internalParticipant) {
        return withArray((p, financeCheckResource) -> financeCheckResource.setInternalParticipant(p), internalParticipant);
    }

    public FinanceCheckProcessResourceBuilder withModifiedDate(ZonedDateTime... modifiedDate) {
        return withArray((d, financeCheckResource) -> financeCheckResource.setModifiedDate(d), modifiedDate);
    }
}
