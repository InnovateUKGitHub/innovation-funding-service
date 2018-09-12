package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.user.domain.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link AssessmentParticipant}s.
 */
public class AssessmentParticipantBuilder extends BaseBuilder<AssessmentParticipant, AssessmentParticipantBuilder> {

    public static AssessmentParticipantBuilder newAssessmentParticipant() {
        return new AssessmentParticipantBuilder(emptyList()).with(uniqueIds());
    }

    private AssessmentParticipantBuilder(List<BiConsumer<Integer, AssessmentParticipant>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AssessmentParticipantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentParticipant>> actions) {
        return new AssessmentParticipantBuilder(actions);
    }

    @Override
    protected AssessmentParticipant createInitial() {
        try {
            Constructor c = AssessmentParticipant.class.getDeclaredConstructor();
            c.setAccessible(true);
            AssessmentParticipant instance = (AssessmentParticipant) c.newInstance();
            setField("status", ParticipantStatus.PENDING, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Missing default constructor for CompetitionParticipant", e);
        }
    }

    public AssessmentParticipantBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public AssessmentParticipantBuilder withStatus(ParticipantStatus... states) {
        return withArray((status, s) -> setField("status", status, s), states);
    }

    public AssessmentParticipantBuilder withRole(CompetitionParticipantRole... roles) {
        return withArray((role, r) -> setField("role", role, r), roles);
    }

    public AssessmentParticipantBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public AssessmentParticipantBuilder withCompetition(Builder<Competition, ?> competitions) {
        return withCompetition(competitions.build());
    }

    public AssessmentParticipantBuilder withInvite(AssessmentInvite... invites) {
        return withArray((invite, i) -> setField("invite", invite, i), invites);
    }

    public AssessmentParticipantBuilder withInvite(Builder<AssessmentInvite, ?> invite) {
        return withInvite(invite.build());
    }

    public AssessmentParticipantBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }

    public AssessmentParticipantBuilder withUser(Builder<User, ?> user) {
        return withUser(user.build());
    }

    public AssessmentParticipantBuilder withRejectionReason(RejectionReason... rejectionReasons) {
        return withArray((rejectionReason, r) -> setField("rejectionReason", rejectionReason, r), rejectionReasons);
    }

    public AssessmentParticipantBuilder withRejectionReason(Builder<RejectionReason, ?> rejectionReason) {
        return withRejectionReason(rejectionReason.build());
    }

    public AssessmentParticipantBuilder withRejectionComment(String... rejectionReasonComments) {
        return withArray((rejectionComment, r) -> setField("rejectionReasonComment", rejectionComment, r), rejectionReasonComments);
    }
}
