package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;

public class CompetitionClosedKeyAssessmentStatisticsResourceDocs {

    public static final CompetitionClosedKeyAssessmentStatisticsResourceBuilder
            competitionClosedKeyAssessmentStatisticsResourceBuilder =
            newCompetitionClosedKeyAssessmentStatisticsResource()
            .withAssessorsAccepted(1)
            .withAssessorsInvited(2)
            .withAssessorsWithoutApplications(3);
}
