package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionClosedKeyAssessmentStatisticsResourceDocs {

    public static final CompetitionClosedKeyAssessmentStatisticsResourceBuilder
            competitionClosedKeyAssessmentStatisticsResourceBuilder =
            newCompetitionClosedKeyAssessmentStatisticsResource()
            .withAssessorsAccepted(1)
            .withAssessorsInvited(2)
            .withAssessorsWithoutApplications(3);
}
