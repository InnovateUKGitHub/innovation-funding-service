package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder;

import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;

public class InterviewStatisticsResourceDocs {
    public static final InterviewStatisticsResourceBuilder INTERVIEW_STATISTICS_RESOURCE_BUILDER =
            newInterviewStatisticsResource()
                    .withApplicationsAssigned(1)
                    .withRespondedToFeedback(2)
                    .withAssessorsAccepted(3);
}