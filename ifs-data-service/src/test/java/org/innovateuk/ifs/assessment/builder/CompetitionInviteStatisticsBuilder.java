package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionInviteStatistics;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionInviteStatisticsBuilder extends BaseBuilder<CompetitionInviteStatistics, CompetitionInviteStatisticsBuilder> {

    private CompetitionInviteStatisticsBuilder(List<BiConsumer<Integer, CompetitionInviteStatistics>> multiActions) {
        super(multiActions);
    }

    public static CompetitionInviteStatisticsBuilder newCompetitionInviteStatistics() {
        return new CompetitionInviteStatisticsBuilder(emptyList());

    }

    @Override
    protected CompetitionInviteStatisticsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionInviteStatistics>> actions) {
        return new CompetitionInviteStatisticsBuilder(actions);
    }

    @Override
    protected CompetitionInviteStatistics createInitial() {
        return new CompetitionInviteStatistics();
    }

    public CompetitionInviteStatisticsBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public CompetitionInviteStatisticsBuilder withCompetitionInvites(List<CompetitionInvite>... competitionInvitess) {
        return withArraySetFieldByReflection("competitionInvites", competitionInvitess);
    }

    public CompetitionInviteStatisticsBuilder withCompetitionParticipants(List<CompetitionParticipant>... competitionParticipantss) {
        return withArraySetFieldByReflection("competitionParticipants", competitionParticipantss);
    }

}
