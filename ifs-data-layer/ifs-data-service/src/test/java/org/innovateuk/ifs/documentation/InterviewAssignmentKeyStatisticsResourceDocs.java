package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder;

import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;

public class InterviewAssignmentKeyStatisticsResourceDocs {

    public static final InterviewAssignmentKeyStatisticsResourceBuilder interviewAssignmentKeyStatisticsResourceBuilder =
            newInterviewAssignmentKeyStatisticsResource()
                    .withApplicationsInCompetition(1)
                    .withApplicationsAssigned(2);
}
