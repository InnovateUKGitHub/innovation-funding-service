package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionClosedKeyAssessmentStatisticsResourceDocs {

    public static final FieldDescriptor[] competitionClosedKeyAssessmentStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who accepted the invite"),
            fieldWithPath("assessorsWithoutApplications").description("The number of assessors without any applications")
    };

    public static final CompetitionClosedKeyAssessmentStatisticsResourceBuilder
            competitionClosedKeyAssessmentStatisticsResourceBuilder =
            newCompetitionClosedKeyAssessmentStatisticsResource()
            .withAssessorsAccepted(1)
            .withAssessorsInvited(2)
            .withAssessorsWithoutApplications(3);
}
