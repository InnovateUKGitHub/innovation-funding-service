package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;

public class CompetitionOpenKeyAssessmentStatisticsResourceDocs {


    public static final CompetitionOpenKeyAssessmentStatisticsResourceBuilder
            competitionOpenKeyAssessmentStatisticsResourceBuilder =
            newCompetitionOpenKeyAssessmentStatisticsResource()
                    .withAssessorsAccepted(1)
                    .withAssessorsInvited(2);
}
