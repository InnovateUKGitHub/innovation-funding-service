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

public class AssessmentPanelParticipantResourceBuilder extends BaseBuilder<AssessmentPanelParticipantResource, AssessmentPanelParticipantResourceBuilder> {

    private AssessmentPanelParticipantResourceBuilder(List<BiConsumer<Integer, AssessmentPanelParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentPanelParticipantResourceBuilder newAssessmentPanelParticipantResource() {
        return new AssessmentPanelParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentPanelParticipantResource createInitial() {
        return new AssessmentPanelParticipantResource();
    }

    @Override
    protected AssessmentPanelParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelParticipantResource>> actions) {
        return new AssessmentPanelParticipantResourceBuilder(actions);
    }

    public AssessmentPanelParticipantResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public AssessmentPanelParticipantResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("userId", users);
    }

    public AssessmentPanelParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competitionId", competitions);
    }

    public AssessmentPanelParticipantResourceBuilder withInvite(AssessmentPanelInviteResource... invites) {
        return withArraySetFieldByReflection("invite", invites);
    }

    public AssessmentPanelParticipantResourceBuilder withInvite(Builder<AssessmentPanelInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public AssessmentPanelParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArraySetFieldByReflection("rejectionReason", rejectionReasons);
    }

    public AssessmentPanelParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArraySetFieldByReflection("rejectionReasonComment", rejectionReasonComments);
    }

    public AssessmentPanelParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArraySetFieldByReflection("role", roles);
    }

    public AssessmentPanelParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public AssessmentPanelParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessmentPanelParticipantResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public AssessmentPanelParticipantResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public AssessmentPanelParticipantResourceBuilder withSubmittedAssessments(Long... submittedAssessmentCounts) {
        return withArraySetFieldByReflection("submittedAssessments", submittedAssessmentCounts);
    }

    public AssessmentPanelParticipantResourceBuilder withTotalAssessments(Long... totalAssessmentCounts) {
        return withArraySetFieldByReflection("totalAssessments", totalAssessmentCounts);
    }

    public AssessmentPanelParticipantResourceBuilder withPendingAssessments(Long... pendingAssessmentCounts) {
        return withArraySetFieldByReflection("pendingAssessments", pendingAssessmentCounts);
    }

    public AssessmentPanelParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    @Override
    protected void postProcess(int index, AssessmentPanelParticipantResource instance) {
        super.postProcess(index, instance);
    }
}
