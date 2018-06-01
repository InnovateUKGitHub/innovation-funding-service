package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionInAssessmentKeyAssessmentStatisticsResourceDocs {

    public static final FieldDescriptor[] competitionInAssessmentKeyAssessmentStatisticsResourceFields = {
            fieldWithPath("assignmentCount").description("The number of assignments"),
            fieldWithPath("assignmentsWaiting").description("The number of assignments waiting response"),
            fieldWithPath("assignmentsAccepted").description("The number of assignments accepted"),
            fieldWithPath("assessmentsStarted").description("The number of assignments started"),
            fieldWithPath("assessmentsSubmitted").description("The number of assignments submitted")
    };

    public static final CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder
            competitionInAssessmentKeyAssessmentStatisticsResourceBuilder =
            newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                    .withAssignmentCount(1)
                    .withAssignmentsWaiting(2)
                    .withAssignmentsAccepted(3)
                    .withAssessmentsStarted(4)
                    .withAssessmentsSubmitted(5);
}
