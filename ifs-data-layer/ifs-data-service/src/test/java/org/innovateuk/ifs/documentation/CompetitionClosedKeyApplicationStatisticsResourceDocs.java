package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder;

import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder.newCompetitionClosedKeyApplicationStatisticsResource;

public class CompetitionClosedKeyApplicationStatisticsResourceDocs {

    public static final CompetitionClosedKeyApplicationStatisticsResourceBuilder
            competitionClosedKeyApplicationStatisticsResourceBuilder =
            newCompetitionClosedKeyApplicationStatisticsResource()
                    .withApplicationsPerAssessor(1)
                    .withApplicationsRequiringAssessors(2)
                    .withAssignmentCount(3);
}
