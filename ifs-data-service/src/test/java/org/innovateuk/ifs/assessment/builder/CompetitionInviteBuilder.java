package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.BuilderAmendFunctions;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public CompetitionInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public CompetitionInviteBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public CompetitionInviteBuilder withStatus(InviteStatus... statuses) {
        return  withArraySetFieldByReflection("status", statuses);
    }

    public CompetitionInviteBuilder withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
    }

    public CompetitionInviteBuilder withUser(Builder<User, ?> users) {
        return withUser(users.build());
    }

    public CompetitionInviteBuilder withHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }

    public CompetitionInviteBuilder withInnovationArea(InnovationArea... innovationAreas) {
        return withArraySetFieldByReflection("innovationArea", innovationAreas);
    }

    public CompetitionInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public CompetitionInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
}
