package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewPanelCreatedInviteResourceBuilder extends BaseBuilder<InterviewPanelCreatedInviteResource, InterviewPanelCreatedInviteResourceBuilder> {

    private InterviewPanelCreatedInviteResourceBuilder(List<BiConsumer<Integer, InterviewPanelCreatedInviteResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected InterviewPanelCreatedInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewPanelCreatedInviteResource>> actions) {
        return new InterviewPanelCreatedInviteResourceBuilder(actions);
    }

    @Override
    protected InterviewPanelCreatedInviteResource createInitial() {
        return new InterviewPanelCreatedInviteResource();
    }

    public static InterviewPanelCreatedInviteResourceBuilder newInterviewPanelCreatedInviteResource() {
        return new InterviewPanelCreatedInviteResourceBuilder(emptyList());
    }

    public InterviewPanelCreatedInviteResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public InterviewPanelCreatedInviteResourceBuilder withApplicationId(Long... value) {
        return withArraySetFieldByReflection("applicationId", value);
    }

    public InterviewPanelCreatedInviteResourceBuilder withApplicationName(String... value) {
        return withArraySetFieldByReflection("applicationName", value);
    }

    public InterviewPanelCreatedInviteResourceBuilder withLeadOrganisationName(String... value) {
        return withArraySetFieldByReflection("leadOrganisationName", value);
    }
}