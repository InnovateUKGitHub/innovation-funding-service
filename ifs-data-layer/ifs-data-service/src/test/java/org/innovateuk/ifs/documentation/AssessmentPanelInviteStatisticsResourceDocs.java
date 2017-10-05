package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.AssessmentPanelInviteStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.AssessmentPanelInviteStatisticsResourceBuilder.newAssessmentPanelInviteStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentPanelInviteStatisticsResourceDocs {
    public static final FieldDescriptor[] assessmentPanelInviteStatisticsResourceFields = {
            fieldWithPath("invited").description("The number of assessors invited to the assessment panel"),
            fieldWithPath("accepted").description("The number of assessors who have accepted an invitation to the assessment panel"),
            fieldWithPath("declined").description("The number of assessors who have rejected an invitation to the assessment panel"),
            fieldWithPath("pending").description("The number of assessors with pending invites")
    };

    public static final AssessmentPanelInviteStatisticsResourceBuilder assessmentPanelInviteStatisticsResourceBuilder =
            newAssessmentPanelInviteStatisticsResource()
                    .withAssessorsInvited(11)
                    .withAssessorsAccepted(3)
                    .withAssessorsRejected(2)
                    .withAssessorsPending(6);
}
