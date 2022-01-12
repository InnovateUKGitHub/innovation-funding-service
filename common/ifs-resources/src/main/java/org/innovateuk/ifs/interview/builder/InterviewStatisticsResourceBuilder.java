package org.innovateuk.ifs.interview.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for InterviewInviteStatisticsResources
 */
public class InterviewStatisticsResourceBuilder
    extends BaseBuilder<InterviewStatisticsResource, InterviewStatisticsResourceBuilder> {

    protected InterviewStatisticsResourceBuilder() {
        super();
    }

    protected InterviewStatisticsResourceBuilder(List<BiConsumer<Integer, InterviewStatisticsResource>> newActions) {
        super(newActions);
    }

    public static InterviewStatisticsResourceBuilder newInterviewStatisticsResource() {
        return new InterviewStatisticsResourceBuilder();
    }

    @Override
    protected InterviewStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewStatisticsResource>> actions) {
        return new InterviewStatisticsResourceBuilder(actions);
    }

    @Override
    protected InterviewStatisticsResource createInitial() {
        return new InterviewStatisticsResource();
    }

    public InterviewStatisticsResourceBuilder withApplicationsAssigned(Integer ...applicationsAssigned) {
        return withArraySetFieldByReflection("applicationsAssigned", applicationsAssigned);
    }

    public InterviewStatisticsResourceBuilder withRespondedToFeedback(Integer ...respondedToFeedback) {
        return withArraySetFieldByReflection("respondedToFeedback", respondedToFeedback);
    }

    public InterviewStatisticsResourceBuilder withAssessorsAccepted(Integer ...assessorsAccepted) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepted);
    }
}