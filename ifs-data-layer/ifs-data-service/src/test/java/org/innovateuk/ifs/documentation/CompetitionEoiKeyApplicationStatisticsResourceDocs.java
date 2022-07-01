package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionEoiKeyApplicationStatisticsResourceBuilder;

import static org.innovateuk.ifs.competition.builder.CompetitionEoiKeyApplicationStatisticsResourceBuilder.newCompetitionEoiKeyApplicationStatisticsResource;

public class CompetitionEoiKeyApplicationStatisticsResourceDocs {

    public static final CompetitionEoiKeyApplicationStatisticsResourceBuilder
            competitionEoiKeyApplicationStatisticsResourceBuilder =
           newCompetitionEoiKeyApplicationStatisticsResource()
                    .withApplicationsSubmitted(1);
}