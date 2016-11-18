package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.CompetitionParticipantRole;
import com.worth.ifs.invite.domain.ParticipantStatus;
import com.worth.ifs.user.domain.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.invite.domain.CompetitionParticipant}s.
 */
public class CompetitionParticipantBuilder extends BaseBuilder<CompetitionParticipant, CompetitionParticipantBuilder> {

    public static CompetitionParticipantBuilder newCompetitionParticipant() {
        return new CompetitionParticipantBuilder(emptyList()).with(uniqueIds());
    }

    private CompetitionParticipantBuilder(List<BiConsumer<Integer, CompetitionParticipant>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CompetitionParticipantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionParticipant>> actions) {
        return new CompetitionParticipantBuilder(actions);
    }

    @Override
    protected CompetitionParticipant createInitial() {
        try {
            Constructor c = CompetitionParticipant.class.getDeclaredConstructor();
            c.setAccessible(true);
            CompetitionParticipant instance = (CompetitionParticipant) c.newInstance();
            setField("status", ParticipantStatus.PENDING, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Missing default constructor for CompetitionParticipant", e);
        }
    }

    public CompetitionParticipantBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public CompetitionParticipantBuilder withStatus(ParticipantStatus... states) {
        return withArray((status, s) -> setField("status", status, s), states);
    }

    public CompetitionParticipantBuilder withRole(CompetitionParticipantRole... roles) {
        return withArray((role, r) -> setField("role", role, r), roles);
    }

    public CompetitionParticipantBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public CompetitionParticipantBuilder withCompetition(Builder<Competition, ?> competitions) {
        return withCompetition(competitions.build());
    }

    public CompetitionParticipantBuilder withInvite(CompetitionInvite... invites) {
        return withArray((invite, i) -> setField("invite", invite, i), invites);
    }

    public CompetitionParticipantBuilder withInvite(Builder<CompetitionInvite, ?> invite) {
        return withInvite(invite.build());
    }

    public CompetitionParticipantBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }

    public CompetitionParticipantBuilder withUser(Builder<User, ?> user) {
        return withUser(user.build());
    }

}
