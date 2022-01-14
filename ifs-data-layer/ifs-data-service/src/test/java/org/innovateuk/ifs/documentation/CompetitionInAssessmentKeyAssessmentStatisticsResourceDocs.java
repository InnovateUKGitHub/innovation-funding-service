package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;

public class CompetitionInAssessmentKeyAssessmentStatisticsResourceDocs {

    public static final CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder
            competitionInAssessmentKeyAssessmentStatisticsResourceBuilder =
            newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                    .withAssignmentCount(1)
                    .withAssignmentsWaiting(2)
                    .withAssignmentsAccepted(3)
                    .withAssessmentsStarted(4)
                    .withAssessmentsSubmitted(5);
}
