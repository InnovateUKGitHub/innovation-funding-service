package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder;

import static org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder.newInterviewInviteStatisticsResource;

public class InterviewInviteStatisticsResourceDocs {

    public static final InterviewInviteStatisticsResourceBuilder interviewInviteStatisticsResourceBuilder =
            newInterviewInviteStatisticsResource()
                    .withAssessorsInvited(3)
                    .withAssessorsAccepted(2)
                    .withAssessorsRejected(1);
}