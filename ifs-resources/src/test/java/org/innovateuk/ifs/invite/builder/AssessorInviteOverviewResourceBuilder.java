package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessorInviteOverviewResourceBuilder extends AssessorInviteResourceBuilder<AssessorInviteOverviewResource, AssessorInviteOverviewResourceBuilder> {

    private AssessorInviteOverviewResourceBuilder(List<BiConsumer<Integer, AssessorInviteOverviewResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessorInviteOverviewResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorInviteOverviewResource>> actions) {
        return new AssessorInviteOverviewResourceBuilder(actions);
    }

    @Override
    protected AssessorInviteOverviewResource createInitial() {
        return new AssessorInviteOverviewResource();
    }

    public static AssessorInviteOverviewResourceBuilder newAssessorInviteOverviewResource() {
        return new AssessorInviteOverviewResourceBuilder(emptyList());
    }

    public AssessorInviteOverviewResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public AssessorInviteOverviewResourceBuilder withBusinessType(BusinessType... value) {
        return withArraySetFieldByReflection("businessType", value);
    }

    public AssessorInviteOverviewResourceBuilder withStatus(ParticipantStatusResource... value) {
        return withArraySetFieldByReflection("status", value);
    }

    public AssessorInviteOverviewResourceBuilder withDetails(String... value) {
        return withArraySetFieldByReflection("details", value);
    }

    public AssessorInviteOverviewResourceBuilder withInviteId(Long... value) {
        return withArraySetFieldByReflection("inviteId", value);
    }
}