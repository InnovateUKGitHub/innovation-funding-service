package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewStatisticsResourceDocs {
    public static final FieldDescriptor[] interviewStatisticsResourceFields = {
            fieldWithPath("applicationsAssigned").description("The number of applications assigned and notified to the interview panel"),
            fieldWithPath("respondedToFeedback").description("The number of applicants that have responded to their feedback"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who have accepted their invites to interview panel")
    };

    public static final InterviewStatisticsResourceBuilder interviewStatisticsResourceBuilder =
            newInterviewStatisticsResource()
                    .withApplicationsAssigned(1)
                    .withRespondedToFeedback(2)
                    .withAssessorsAccepted(3);
}
