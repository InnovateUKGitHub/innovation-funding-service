package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.invite.resource.InterviewInviteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link InterviewInviteResource}
 */
public class InterviewApplicationSentInviteResourceBuilder extends BaseBuilder<InterviewApplicationSentInviteResource, InterviewApplicationSentInviteResourceBuilder> {

    private InterviewApplicationSentInviteResourceBuilder(List<BiConsumer<Integer, InterviewApplicationSentInviteResource>> multiActions) {
        super(multiActions);
    }

    public static InterviewApplicationSentInviteResourceBuilder newInterviewApplicationSentInviteResource() {
        return new InterviewApplicationSentInviteResourceBuilder(emptyList());
    }

    public InterviewApplicationSentInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public InterviewApplicationSentInviteResourceBuilder withSubject(String... subject) {
        return withArraySetFieldByReflection("subject", subject);
    }

    public InterviewApplicationSentInviteResourceBuilder withContent(String... content) {
        return withArraySetFieldByReflection("content", content);
    }

    public InterviewApplicationSentInviteResourceBuilder withAssigned(ZonedDateTime... assigned) {
        return withArraySetFieldByReflection("assigned", assigned);
    }

    @Override
    protected InterviewApplicationSentInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewApplicationSentInviteResource>> actions) {
        return new InterviewApplicationSentInviteResourceBuilder(actions);
    }

    @Override
    protected InterviewApplicationSentInviteResource createInitial() {
        return new InterviewApplicationSentInviteResource();
    }
}
