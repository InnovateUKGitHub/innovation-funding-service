package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionFundedKeyApplicationStatisticsResourceBuilder;

import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyApplicationStatisticsResourceBuilder.newCompetitionFundedKeyApplicationStatisticsResource;

public class CompetitionFundedKeyApplicationStatisticsResourceDocs {

    public static final CompetitionFundedKeyApplicationStatisticsResourceBuilder
            competitionFundedKeyApplicationStatisticsResourceBuilder =
            newCompetitionFundedKeyApplicationStatisticsResource()
                    .withApplicationsSubmitted(1)
                    .withApplicationsFunded(2)
                    .withApplicationsNotFunded(3)
                    .withApplicationsOnHold(4)
                    .withApplicationsNotifiedOfDecision(5)
                    .withApplicationsAwaitingDecision(6);
}