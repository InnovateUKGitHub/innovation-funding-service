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

public class AssessmentInterviewPanelParticipantResourceBuilder extends BaseBuilder<AssessmentInterviewPanelParticipantResource, AssessmentInterviewPanelParticipantResourceBuilder> {

    private AssessmentInterviewPanelParticipantResourceBuilder(List<BiConsumer<Integer, AssessmentInterviewPanelParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInterviewPanelParticipantResourceBuilder newAssessmentInterviewPanelParticipantResource() {
        return new AssessmentInterviewPanelParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentInterviewPanelParticipantResource createInitial() {
        return new AssessmentInterviewPanelParticipantResource();
    }

    @Override
    protected AssessmentInterviewPanelParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentInterviewPanelParticipantResource>> actions) {
        return new AssessmentInterviewPanelParticipantResourceBuilder(actions);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("userId", users);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competitionId", competitions);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withInvite(AssessmentInterviewPanelInviteResource... invites) {
        return withArraySetFieldByReflection("invite", invites);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withInvite(Builder<AssessmentInterviewPanelInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArraySetFieldByReflection("rejectionReason", rejectionReasons);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArraySetFieldByReflection("rejectionReasonComment", rejectionReasonComments);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArraySetFieldByReflection("role", roles);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withSubmittedAssessments(Long... submittedAssessmentCounts) {
        return withArraySetFieldByReflection("submittedAssessments", submittedAssessmentCounts);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withTotalAssessments(Long... totalAssessmentCounts) {
        return withArraySetFieldByReflection("totalAssessments", totalAssessmentCounts);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withPendingAssessments(Long... pendingAssessmentCounts) {
        return withArraySetFieldByReflection("pendingAssessments", pendingAssessmentCounts);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    public AssessmentInterviewPanelParticipantResourceBuilder withAwaitingApplications(Long... awaitingApplications) {
        return withArraySetFieldByReflection("awaitingApplications", awaitingApplications);
    }

    @Override
    protected void postProcess(int index, AssessmentInterviewPanelParticipantResource instance) {
        super.postProcess(index, instance);
    }
}
