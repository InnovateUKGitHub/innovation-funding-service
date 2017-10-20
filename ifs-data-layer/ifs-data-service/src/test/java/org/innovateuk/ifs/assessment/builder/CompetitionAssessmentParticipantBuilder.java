package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.user.domain.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant}s.
 */
public class CompetitionAssessmentParticipantBuilder extends BaseBuilder<CompetitionAssessmentParticipant, CompetitionAssessmentParticipantBuilder> {

    public static CompetitionAssessmentParticipantBuilder newCompetitionAssessmentParticipant() {
        return new CompetitionAssessmentParticipantBuilder(emptyList()).with(uniqueIds());
    }

    private CompetitionAssessmentParticipantBuilder(List<BiConsumer<Integer, CompetitionAssessmentParticipant>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CompetitionAssessmentParticipantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionAssessmentParticipant>> actions) {
        return new CompetitionAssessmentParticipantBuilder(actions);
    }

    @Override
    protected CompetitionAssessmentParticipant createInitial() {
        try {
            Constructor c = CompetitionAssessmentParticipant.class.getDeclaredConstructor();
            c.setAccessible(true);
            CompetitionAssessmentParticipant instance = (CompetitionAssessmentParticipant) c.newInstance();
            setField("status", ParticipantStatus.PENDING, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Missing default constructor for CompetitionParticipant", e);
        }
    }

    public CompetitionAssessmentParticipantBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public CompetitionAssessmentParticipantBuilder withStatus(ParticipantStatus... states) {
        return withArray((status, s) -> setField("status", status, s), states);
    }

    public CompetitionAssessmentParticipantBuilder withRole(CompetitionParticipantRole... roles) {
        return withArray((role, r) -> setField("role", role, r), roles);
    }

    public CompetitionAssessmentParticipantBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public CompetitionAssessmentParticipantBuilder withCompetition(Builder<Competition, ?> competitions) {
        return withCompetition(competitions.build());
    }

    public CompetitionAssessmentParticipantBuilder withInvite(CompetitionInvite... invites) {
        return withArray((invite, i) -> setField("invite", invite, i), invites);
    }

    public CompetitionAssessmentParticipantBuilder withInvite(Builder<CompetitionInvite, ?> invite) {
        return withInvite(invite.build());
    }

    public CompetitionAssessmentParticipantBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }

    public CompetitionAssessmentParticipantBuilder withUser(Builder<User, ?> user) {
        return withUser(user.build());
    }

    public CompetitionAssessmentParticipantBuilder withRejectionReason(RejectionReason... rejectionReasons) {
        return withArray((rejectionReason, r) -> setField("rejectionReason", rejectionReason, r), rejectionReasons);
    }

    public CompetitionAssessmentParticipantBuilder withRejectionReason(Builder<RejectionReason, ?> rejectionReason) {
        return withRejectionReason(rejectionReason.build());
    }

    public CompetitionAssessmentParticipantBuilder withRejectionComment(String... rejectionReasonComments) {
        return withArray((rejectionComment, r) -> setField("rejectionReasonComment", rejectionComment, r), rejectionReasonComments);
    }
}
