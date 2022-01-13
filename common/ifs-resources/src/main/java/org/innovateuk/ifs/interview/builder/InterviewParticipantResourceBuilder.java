package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InterviewParticipantResourceBuilder extends BaseBuilder<InterviewParticipantResource, InterviewParticipantResourceBuilder> {

    private InterviewParticipantResourceBuilder(List<BiConsumer<Integer, InterviewParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static InterviewParticipantResourceBuilder newInterviewParticipantResource() {
        return new InterviewParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InterviewParticipantResource createInitial() {
        return new InterviewParticipantResource();
    }

    @Override
    protected InterviewParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewParticipantResource>> actions) {
        return new InterviewParticipantResourceBuilder(actions);
    }

    public InterviewParticipantResourceBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public InterviewParticipantResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("userId", users);
    }

    public InterviewParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArraySetFieldByReflection("competitionId", competitions);
    }

    public InterviewParticipantResourceBuilder withInvite(InterviewInviteResource... invites) {
        return withArraySetFieldByReflection("invite", invites);
    }

    public InterviewParticipantResourceBuilder withInvite(Builder<InterviewInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public InterviewParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArraySetFieldByReflection("rejectionReason", rejectionReasons);
    }

    public InterviewParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArraySetFieldByReflection("rejectionReasonComment", rejectionReasonComments);
    }

    public InterviewParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArraySetFieldByReflection("role", roles);
    }

    public InterviewParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public InterviewParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public InterviewParticipantResourceBuilder withAssessorAcceptsDate(ZonedDateTime... assessorAcceptsDates) {
        return withArraySetFieldByReflection("assessorAcceptsDate", assessorAcceptsDates);
    }

    public InterviewParticipantResourceBuilder withAssessorDeadlineDate(ZonedDateTime... assessorDeadlineDates) {
        return withArraySetFieldByReflection("assessorDeadlineDate", assessorDeadlineDates);
    }

    public InterviewParticipantResourceBuilder withSubmittedAssessments(Long... submittedAssessmentCounts) {
        return withArraySetFieldByReflection("submittedAssessments", submittedAssessmentCounts);
    }

    public InterviewParticipantResourceBuilder withTotalAssessments(Long... totalAssessmentCounts) {
        return withArraySetFieldByReflection("totalAssessments", totalAssessmentCounts);
    }

    public InterviewParticipantResourceBuilder withPendingAssessments(Long... pendingAssessmentCounts) {
        return withArraySetFieldByReflection("pendingAssessments", pendingAssessmentCounts);
    }

    public InterviewParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    public InterviewParticipantResourceBuilder withAwaitingApplications(Long... awaitingApplications) {
        return withArraySetFieldByReflection("awaitingApplications", awaitingApplications);
    }

    @Override
    protected void postProcess(int index, InterviewParticipantResource instance) {
        super.postProcess(index, instance);
    }
}
