package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
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
 * Builder for {@link InterviewParticipant}s.
 */
public class InterviewParticipantBuilder extends BaseBuilder<InterviewParticipant, InterviewParticipantBuilder> {

    public static InterviewParticipantBuilder newInterviewParticipant() {
        return new InterviewParticipantBuilder(emptyList()).with(uniqueIds());
    }

    private InterviewParticipantBuilder(List<BiConsumer<Integer, InterviewParticipant>> multiActions) {
        super(multiActions);
    }

    @Override
    protected InterviewParticipantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewParticipant>> actions) {
        return new InterviewParticipantBuilder(actions);
    }

    @Override
    protected InterviewParticipant createInitial() {
        try {
            Constructor c = InterviewParticipant.class.getDeclaredConstructor();
            c.setAccessible(true);
            InterviewParticipant instance = (InterviewParticipant) c.newInstance();
            setField("status", ParticipantStatus.PENDING, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Missing default constructor for InterviewParticipant", e);
        }
    }

    public InterviewParticipantBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public InterviewParticipantBuilder withStatus(ParticipantStatus... states) {
        return withArray((status, s) -> setField("status", status, s), states);
    }

    public InterviewParticipantBuilder withRole(CompetitionParticipantRole... roles) {
        return withArray((role, r) -> setField("role", role, r), roles);
    }

    public InterviewParticipantBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public InterviewParticipantBuilder withCompetition(Builder<Competition, ?> competitions) {
        return withCompetition(competitions.build());
    }

    public InterviewParticipantBuilder withInvite(InterviewInvite... invites) {
        return withArray((invite, i) -> setField("invite", invite, i), invites);
    }

    public InterviewParticipantBuilder withInvite(Builder<InterviewInvite, ?> invite) {
        return withInvite(invite.build());
    }

    public InterviewParticipantBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }

    public InterviewParticipantBuilder withUser(Builder<User, ?> user) {
        return withUser(user.build());
    }

    public InterviewParticipantBuilder withRejectionReason(RejectionReason... rejectionReasons) {
        return withArray((rejectionReason, r) -> setField("rejectionReason", rejectionReason, r), rejectionReasons);
    }

    public InterviewParticipantBuilder withRejectionReason(Builder<RejectionReason, ?> rejectionReason) {
        return withRejectionReason(rejectionReason.build());
    }

    public InterviewParticipantBuilder withRejectionComment(String... rejectionReasonComments) {
        return withArray((rejectionComment, r) -> setField("rejectionReasonComment", rejectionComment, r), rejectionReasonComments);
    }
}
