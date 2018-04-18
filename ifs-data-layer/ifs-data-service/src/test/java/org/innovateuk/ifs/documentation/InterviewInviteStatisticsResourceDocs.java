package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder.newInterviewInviteStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewInviteStatisticsResourceDocs {
    public static final FieldDescriptor[] interviewInviteStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited to the interview panel"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who have accepted an invitation to the invterview panel"),
            fieldWithPath("assessorsRejected").description("The number of assessors who have rejected an invitation to the interview panel")
    };

    public static final InterviewInviteStatisticsResourceBuilder interviewInviteStatisticsResourceBuilder =
            newInterviewInviteStatisticsResource()
                    .withAssessorsInvited(3)
                    .withAssessorsAccepted(2)
                    .withAssessorsRejected(1);
}