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

public class AssessmentReviewPanelParticipantResourceBuilder extends BaseBuilder<ReviewParticipantResource, AssessmentReviewPanelParticipantResourceBuilder> {

    private AssessmentReviewPanelParticipantResourceBuilder(List<BiConsumer<Integer, ReviewParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewPanelParticipantResourceBuilder newAssessmentReviewPanelParticipantResource() {
        return new AssessmentReviewPanelParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ReviewParticipantResource createInitial() {
        return new ReviewParticipantResource();
    }

    @Override
    protected AssessmentReviewPanelParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewParticipantResource>> actions) {
        return new AssessmentReviewPanelParticipantResourceBuilder(actions);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("userId", users);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competitionId", competitions);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withInvite(ReviewInviteResource... invites) {
        return withArraySetFieldByReflection("invite", invites);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withInvite(Builder<ReviewInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public AssessmentReviewPanelParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArraySetFieldByReflection("rejectionReason", rejectionReasons);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArraySetFieldByReflection("rejectionReasonComment", rejectionReasonComments);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArraySetFieldByReflection("role", roles);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withSubmittedAssessments(Long... submittedAssessmentCounts) {
        return withArraySetFieldByReflection("submittedAssessments", submittedAssessmentCounts);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withTotalAssessments(Long... totalAssessmentCounts) {
        return withArraySetFieldByReflection("totalAssessments", totalAssessmentCounts);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withPendingAssessments(Long... pendingAssessmentCounts) {
        return withArraySetFieldByReflection("pendingAssessments", pendingAssessmentCounts);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    public AssessmentReviewPanelParticipantResourceBuilder withAwaitingApplications(Long... awaitingApplications) {
        return withArraySetFieldByReflection("awaitingApplications", awaitingApplications);
    }

    @Override
    protected void postProcess(int index, ReviewParticipantResource instance) {
        super.postProcess(index, instance);
    }
}
