package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link InterviewAssignmentKeyStatisticsResource}
 */
public class InterviewAssignmentKeyStatisticsResourceBuilder extends BaseBuilder<InterviewAssignmentKeyStatisticsResource, InterviewAssignmentKeyStatisticsResourceBuilder> {

    private InterviewAssignmentKeyStatisticsResourceBuilder(List<BiConsumer<Integer, InterviewAssignmentKeyStatisticsResource>> multiActions) {
        super(multiActions);
    }

    public static InterviewAssignmentKeyStatisticsResourceBuilder newInterviewAssignmentKeyStatisticsResource() {
        return new InterviewAssignmentKeyStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected InterviewAssignmentKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentKeyStatisticsResource>> actions) {
        return new InterviewAssignmentKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected InterviewAssignmentKeyStatisticsResource createInitial() {
        return new InterviewAssignmentKeyStatisticsResource();
    }

    public InterviewAssignmentKeyStatisticsResourceBuilder withApplicationsInCompetition(Integer... applicationsInCompetition) {
        return withArraySetFieldByReflection("applicationsInCompetition", applicationsInCompetition);
    }

    public InterviewAssignmentKeyStatisticsResourceBuilder withApplicationsAssigned(Integer... applicationsAssigned) {
        return withArraySetFieldByReflection("applicationsAssigned", applicationsAssigned);
    }
}
