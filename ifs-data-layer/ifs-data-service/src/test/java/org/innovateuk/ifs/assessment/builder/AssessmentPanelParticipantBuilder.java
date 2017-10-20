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
 * Builder for {@link CompetitionAssessmentParticipant}s.
 */
public class AssessmentPanelParticipantBuilder extends BaseBuilder<AssessmentPanelParticipant, AssessmentPanelParticipantBuilder> {

    public static AssessmentPanelParticipantBuilder newCompetitionAssessmentParticipant() {
        return new AssessmentPanelParticipantBuilder(emptyList()).with(uniqueIds());
    }

    private AssessmentPanelParticipantBuilder(List<BiConsumer<Integer, AssessmentPanelParticipant>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AssessmentPanelParticipantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelParticipant>> actions) {
        return new AssessmentPanelParticipantBuilder(actions);
    }

    @Override
    protected AssessmentPanelParticipant createInitial() {
        try {
            Constructor c = AssessmentPanelParticipant.class.getDeclaredConstructor();
            c.setAccessible(true);
            AssessmentPanelParticipant instance = (AssessmentPanelParticipant) c.newInstance();
            setField("status", ParticipantStatus.PENDING, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Missing default constructor for CompetitionParticipant", e);
        }
    }

    public AssessmentPanelParticipantBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public AssessmentPanelParticipantBuilder withStatus(ParticipantStatus... states) {
        return withArray((status, s) -> setField("status", status, s), states);
    }

    public AssessmentPanelParticipantBuilder withRole(CompetitionParticipantRole... roles) {
        return withArray((role, r) -> setField("role", role, r), roles);
    }

    public AssessmentPanelParticipantBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public AssessmentPanelParticipantBuilder withCompetition(Builder<Competition, ?> competitions) {
        return withCompetition(competitions.build());
    }

    public AssessmentPanelParticipantBuilder withInvite(AssessmentPanelInvite... invites) {
        return withArray((invite, i) -> setField("invite", invite, i), invites);
    }

    public AssessmentPanelParticipantBuilder withInvite(Builder<AssessmentPanelInvite, ?> invite) {
        return withInvite(invite.build());
    }

    public AssessmentPanelParticipantBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }

    public AssessmentPanelParticipantBuilder withUser(Builder<User, ?> user) {
        return withUser(user.build());
    }

    public AssessmentPanelParticipantBuilder withRejectionReason(RejectionReason... rejectionReasons) {
        return withArray((rejectionReason, r) -> setField("rejectionReason", rejectionReason, r), rejectionReasons);
    }

    public AssessmentPanelParticipantBuilder withRejectionReason(Builder<RejectionReason, ?> rejectionReason) {
        return withRejectionReason(rejectionReason.build());
    }

    public AssessmentPanelParticipantBuilder withRejectionComment(String... rejectionReasonComments) {
        return withArray((rejectionComment, r) -> setField("rejectionReasonComment", rejectionComment, r), rejectionReasonComments);
    }
}
