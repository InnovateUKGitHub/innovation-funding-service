package org.innovateuk.ifs.interview.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for InterviewInviteStatisticsResources
 */
public class InterviewInviteStatisticsResourceBuilder
    extends BaseBuilder<InterviewInviteStatisticsResource, InterviewInviteStatisticsResourceBuilder> {

    protected InterviewInviteStatisticsResourceBuilder() {
        super();
    }

    protected InterviewInviteStatisticsResourceBuilder(List<BiConsumer<Integer, InterviewInviteStatisticsResource>> newActions) {
        super(newActions);
    }

    public static InterviewInviteStatisticsResourceBuilder newInterviewInviteStatisticsResource() {
        return new InterviewInviteStatisticsResourceBuilder();
    }

    @Override
    protected InterviewInviteStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewInviteStatisticsResource>> actions) {
        return new InterviewInviteStatisticsResourceBuilder(actions);
    }

    @Override
    protected InterviewInviteStatisticsResource createInitial() {
        return new InterviewInviteStatisticsResource();
    }

    public InterviewInviteStatisticsResourceBuilder withAssessorsInvited(Integer ...assessorsInvited) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInvited);
    }

    public InterviewInviteStatisticsResourceBuilder withAssessorsAccepted(Integer ...assessorsAccepted) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepted);
    }

    public InterviewInviteStatisticsResourceBuilder withAssessorsRejected(Integer ...assessorsDeclined) {
        return withArraySetFieldByReflection("assessorsRejected", assessorsDeclined);
    }
}