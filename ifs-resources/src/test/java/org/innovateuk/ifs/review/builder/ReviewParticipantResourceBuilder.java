package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ReviewParticipantResourceBuilder extends BaseBuilder<ReviewParticipantResource, ReviewParticipantResourceBuilder> {

    private ReviewParticipantResourceBuilder(List<BiConsumer<Integer, ReviewParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static ReviewParticipantResourceBuilder newReviewParticipantResource() {
        return new ReviewParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ReviewParticipantResource createInitial() {
        return new ReviewParticipantResource();
    }

    @Override
    protected ReviewParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewParticipantResource>> actions) {
        return new ReviewParticipantResourceBuilder(actions);
    }

    public ReviewParticipantResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public ReviewParticipantResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("userId", users);
    }

    public ReviewParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competitionId", competitions);
    }

    public ReviewParticipantResourceBuilder withInvite(ReviewInviteResource... invites) {
        return withArraySetFieldByReflection("invite", invites);
    }

    public ReviewParticipantResourceBuilder withInvite(Builder<ReviewInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public ReviewParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArraySetFieldByReflection("rejectionReason", rejectionReasons);
    }

    public ReviewParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArraySetFieldByReflection("rejectionReasonComment", rejectionReasonComments);
    }

    public ReviewParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArraySetFieldByReflection("role", roles);
    }

    public ReviewParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public ReviewParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public ReviewParticipantResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public ReviewParticipantResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public ReviewParticipantResourceBuilder withSubmittedAssessments(Long... submittedAssessmentCounts) {
        return withArraySetFieldByReflection("submittedAssessments", submittedAssessmentCounts);
    }

    public ReviewParticipantResourceBuilder withTotalAssessments(Long... totalAssessmentCounts) {
        return withArraySetFieldByReflection("totalAssessments", totalAssessmentCounts);
    }

    public ReviewParticipantResourceBuilder withPendingAssessments(Long... pendingAssessmentCounts) {
        return withArraySetFieldByReflection("pendingAssessments", pendingAssessmentCounts);
    }

    public ReviewParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    public ReviewParticipantResourceBuilder withAwaitingApplications(Long... awaitingApplications) {
        return withArraySetFieldByReflection("awaitingApplications", awaitingApplications);
    }

    @Override
    protected void postProcess(int index, ReviewParticipantResource instance) {
        super.postProcess(index, instance);
    }
}
