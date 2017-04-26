package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionParticipantResourceBuilder extends BaseBuilder<CompetitionParticipantResource, CompetitionParticipantResourceBuilder> {

    private CompetitionParticipantResourceBuilder(List<BiConsumer<Integer, CompetitionParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static CompetitionParticipantResourceBuilder newCompetitionParticipantResource() {
        return new CompetitionParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionParticipantResource createInitial() {
        return new CompetitionParticipantResource();
    }

    @Override
    protected CompetitionParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionParticipantResource>> actions) {
        return new CompetitionParticipantResourceBuilder(actions);
    }

    public CompetitionParticipantResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public CompetitionParticipantResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("userId", users);
    }

    public CompetitionParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competitionId", competitions);
    }

    public CompetitionParticipantResourceBuilder withInvite(CompetitionInviteResource... invites) {
        return withArraySetFieldByReflection("invite", invites);
    }

    public CompetitionParticipantResourceBuilder withInvite(Builder<CompetitionInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public CompetitionParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArraySetFieldByReflection("rejectionReason", rejectionReasons);
    }

    public CompetitionParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArraySetFieldByReflection("rejectionReasonComment", rejectionReasonComments);
    }

    public CompetitionParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArraySetFieldByReflection("role", roles);
    }

    public CompetitionParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public CompetitionParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public CompetitionParticipantResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public CompetitionParticipantResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public CompetitionParticipantResourceBuilder withSubmittedAssessments(Long... submittedAssessmentCounts) {
        return withArraySetFieldByReflection("submittedAssessments", submittedAssessmentCounts);
    }

    public CompetitionParticipantResourceBuilder withTotalAssessments(Long... totalAssessmentCounts) {
        return withArraySetFieldByReflection("totalAssessments", totalAssessmentCounts);
    }

    public CompetitionParticipantResourceBuilder withPendingAssessments(Long... pendingAssessmentCounts) {
        return withArraySetFieldByReflection("pendingAssessments", pendingAssessmentCounts);
    }

    public CompetitionParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    @Override
    protected void postProcess(int index, CompetitionParticipantResource instance) {
        super.postProcess(index, instance);
    }
}
