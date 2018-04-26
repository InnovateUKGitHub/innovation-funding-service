package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewAssignmentKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] interviewAssignmentKeyStatisticsResourceFields = {
            fieldWithPath("applicationsInCompetition").description("The total number of submitted, eligible applications in the competition"),
            fieldWithPath("applicationsAssigned").description("The number of applications assigned and notified to the interview panel")
    };

    public static final InterviewAssignmentKeyStatisticsResourceBuilder interviewAssignmentKeyStatisticsResourceBuilder =
            newInterviewAssignmentKeyStatisticsResource()
                    .withApplicationsInCompetition(1)
                    .withApplicationsAssigned(2);
}
