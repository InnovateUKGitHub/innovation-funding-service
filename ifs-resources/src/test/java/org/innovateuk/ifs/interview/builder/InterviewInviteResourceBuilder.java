package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InterviewInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link InterviewInviteResource}
 */
public class InterviewInviteResourceBuilder extends BaseBuilder<InterviewInviteResource, InterviewInviteResourceBuilder> {

    private InterviewInviteResourceBuilder(List<BiConsumer<Integer, InterviewInviteResource>> multiActions) {
        super(multiActions);
    }

    public static InterviewInviteResourceBuilder newInterviewInviteResource() {
        return new InterviewInviteResourceBuilder(emptyList());
    }

    public InterviewInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public InterviewInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public InterviewInviteResourceBuilder withCompetitionId(Long... ids) {
        return withArraySetFieldByReflection("competitionId", ids);
    }

    public InterviewInviteResourceBuilder withUserId(Long... ids) {
        return withArraySetFieldByReflection("userId", ids);
    }

    public InterviewInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public InterviewInviteResourceBuilder withInviteHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }

    public InterviewInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    @Override
    protected InterviewInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewInviteResource>> actions) {
        return new InterviewInviteResourceBuilder(actions);
    }

    @Override
    protected InterviewInviteResource createInitial() {
        return new InterviewInviteResource();
    }
}
