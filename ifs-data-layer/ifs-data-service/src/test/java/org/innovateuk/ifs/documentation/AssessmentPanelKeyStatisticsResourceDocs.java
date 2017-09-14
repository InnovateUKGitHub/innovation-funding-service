package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.AssessmentPanelKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.AssessmentPanelKeyStatisticsResourceBuilder.newAssessmentPanelKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentPanelKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] assessmentPanelKeyStatisticsResourceFields = {
            fieldWithPath("applicationsInPanel").description("The number of applications in the assessment panel"),
            fieldWithPath("assessorsPending").description("The number of assessors who haven't responded to an invitation to the assessment panel"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who have accepted an invitation to the assessment panel")
    };

    public static final AssessmentPanelKeyStatisticsResourceBuilder assessmentPanelKeyStatisticsResourceBuilder =
            newAssessmentPanelKeyStatisticsResource()
                    .withApplicationsInPanel(5)
                    .withAssessorsAccepted(3)
                    .withAssessorsPending(2);
}
