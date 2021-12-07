package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder;

import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;

public class CompetitionInviteStatisticsResourceDocs {

    public static final CompetitionInviteStatisticsResourceBuilder competitionInviteStatisticsResourceBuilder =
            newCompetitionInviteStatisticsResource()
                    .withInviteList(1)
                    .withInvited(2)
                    .withAccepted(3)
                    .withDeclined(4);
}
