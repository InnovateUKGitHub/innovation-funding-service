package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.domain.CompetitionInvite;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link CompetitionInvite}s.
 */
public class CompetitionInviteBuilder extends BaseBuilder<CompetitionInvite, CompetitionInviteBuilder> {

    private CompetitionInviteBuilder(List<BiConsumer<Integer, CompetitionInvite>> multiActions) {
        super(multiActions);
    }

    public static CompetitionInviteBuilder newCompetitionInvite() {
        return new CompetitionInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionInvite>> actions) {
        return new CompetitionInviteBuilder(actions);
    }

    public CompetitionInviteBuilder withCompetition(Competition... competitions) {
        return withArray((competition, invite) -> invite.setTarget(competition), competitions);
    }

    public CompetitionInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    @Override
    protected CompetitionInvite createInitial() {
        return new CompetitionInvite();
    }
}
