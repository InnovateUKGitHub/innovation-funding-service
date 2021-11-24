package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewStatisticsResourceDocs {
    public static final InterviewStatisticsResourceBuilder INTERVIEW_STATISTICS_RESOURCE_BUILDER =
            newInterviewStatisticsResource()
                    .withApplicationsAssigned(1)
                    .withRespondedToFeedback(2)
                    .withAssessorsAccepted(3);
}