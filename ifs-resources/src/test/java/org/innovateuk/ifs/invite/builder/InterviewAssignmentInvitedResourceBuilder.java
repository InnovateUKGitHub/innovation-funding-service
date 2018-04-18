package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewAssignmentInvitedResourceBuilder extends BaseBuilder<InterviewAssignmentApplicationResource, InterviewAssignmentInvitedResourceBuilder> {

    private InterviewAssignmentInvitedResourceBuilder(List<BiConsumer<Integer, InterviewAssignmentApplicationResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewAssignmentInvitedResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentApplicationResource>> actions) {
        return new InterviewAssignmentInvitedResourceBuilder(actions);
    }

    @Override
    protected InterviewAssignmentApplicationResource createInitial() {
        return new InterviewAssignmentApplicationResource();
    }

    public static InterviewAssignmentInvitedResourceBuilder newInterviewAssignmentApplicationResource() {
        return new InterviewAssignmentInvitedResourceBuilder(emptyList());
    }

    public InterviewAssignmentInvitedResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewAssignmentInvitedResourceBuilder withApplicationId(Long... value) {
        return withArraySetFieldByReflection("applicationId", value);
    }

    public InterviewAssignmentInvitedResourceBuilder withApplicationName(String... value) {
        return withArraySetFieldByReflection("applicationName", value);
    }

    public InterviewAssignmentInvitedResourceBuilder withLeadOrganisationName(String... value) {
        return withArraySetFieldByReflection("leadOrganisationName", value);
    }

    public InterviewAssignmentInvitedResourceBuilder withStatus(InterviewAssignmentState... value) {
        return withArraySetFieldByReflection("status", value);
    }
}