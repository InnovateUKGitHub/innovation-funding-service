package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public static CompetitionInviteBuilder newCompetitionInviteWithoutId() {
        return new CompetitionInviteBuilder(emptyList());
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

    public CompetitionInviteBuilder withId(Long... ids) {
        return withArray(BuilderAmendFunctions::setId, ids);
    }

    public CompetitionInviteBuilder withName(String... names) {
        return withArray((name, invite) -> setField("name", name, invite), names);
    }

    public CompetitionInviteBuilder withEmail(String... emails) {
        return withArray((email, invite) -> setField("email", email, invite), emails);
    }

    public CompetitionInviteBuilder withStatus(InviteStatus... statuses) {
        return withArray((status, invite) -> setField("status", status, invite), statuses);
    }

    public CompetitionInviteBuilder withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
    }

    public CompetitionInviteBuilder withUser(Builder<User, ?> users) {
        return withUser(users.build());
    }

    public CompetitionInviteBuilder withHash(String... hashes) {
        return withArray((hash, invite) -> setField("hash", hash, invite), hashes);
    }
}
