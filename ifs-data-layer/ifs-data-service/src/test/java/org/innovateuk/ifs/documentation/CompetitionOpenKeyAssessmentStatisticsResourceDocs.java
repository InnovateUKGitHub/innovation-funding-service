package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionOpenKeyAssessmentStatisticsResourceDocs {

    public static final FieldDescriptor[] competitionOpenKeyAssessmentStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who accepted the invite")
    };

    public static final CompetitionOpenKeyAssessmentStatisticsResourceBuilder
            competitionOpenKeyAssessmentStatisticsResourceBuilder =
            newCompetitionOpenKeyAssessmentStatisticsResource()
                    .withAssessorsAccepted(1)
                    .withAssessorsInvited(2);
}
