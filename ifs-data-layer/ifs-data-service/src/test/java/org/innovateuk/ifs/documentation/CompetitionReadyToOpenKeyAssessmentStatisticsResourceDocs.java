package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionReadyToOpenKeyAssessmentStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionReadyToOpenKeyAssessmentStatisticsResourceDocs {

    public static final FieldDescriptor[] competitionReadyToOpenKeyAssessmentStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who accepted the invite")
    };

    public static final CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder
            competitionReadyToOpenKeyAssessmentStatisticsResourceBuilder =
            newCompetitionReadyToOpenKeyAssessmentStatisticsResource()
                    .withAssessorsAccepted(1)
                    .withAssessorsInvited(2);

}
