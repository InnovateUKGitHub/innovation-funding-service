package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.*;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Assessor Panel Dashboard view.
 */
@Component
public class AssessorDashboardModelPopulator {

    private CompetitionParticipantRestService competitionParticipantRestService;

    private InterviewInviteRestService interviewInviteRestService;

    private ProfileRestService profileRestService;

    private ReviewInviteRestService reviewInviteRestService;

    private CompetitionRestService competitionRestService;

    public AssessorDashboardModelPopulator(CompetitionParticipantRestService competitionParticipantRestService,
                                           InterviewInviteRestService interviewInviteRestService,
                                           ProfileRestService profileRestService,
                                           ReviewInviteRestService reviewInviteRestService,
                                           CompetitionRestService competitionRestService) {
        this.competitionParticipantRestService = competitionParticipantRestService;
        this.interviewInviteRestService = interviewInviteRestService;
        this.profileRestService = profileRestService;
        this.reviewInviteRestService = reviewInviteRestService;
        this.competitionRestService = competitionRestService;
    }

    public AssessorDashboardViewModel populateModel(long userId) {
        List<CompetitionParticipantResource> participantResourceList = competitionParticipantRestService
                .getParticipants(userId, CompetitionParticipantRoleResource.ASSESSOR).getSuccess();

        UserProfileStatusResource profileStatusResource = profileRestService.getUserProfileStatus(userId).getSuccess();

        List<ReviewParticipantResource> reviewParticipantResourceList = reviewInviteRestService.getAllInvitesByUser(userId).getSuccess();

        List<InterviewParticipantResource> interviewParticipantResourceList = interviewInviteRestService.getAllInvitesByUser(userId).getSuccess();

        return new AssessorDashboardViewModel(
                getProfileStatus(profileStatusResource),
                getActiveCompetitions(participantResourceList),
                getUpcomingCompetitions(participantResourceList),
                getPendingParticipations(participantResourceList),
                getAssessmentPanelInvites(reviewParticipantResourceList),
                getAssessmentPanelAccepted(reviewParticipantResourceList),
                getInterviewPanelInvites(interviewParticipantResourceList),
                getInterviewPanelAccepted(interviewParticipantResourceList)
                );
    }

    private AssessorProfileStatusViewModel getProfileStatus(UserProfileStatusResource assessorProfileStatusResource) {
        return new AssessorProfileStatusViewModel(assessorProfileStatusResource);
    }

    private List<AssessorDashboardActiveCompetitionViewModel> getActiveCompetitions(List<CompetitionParticipantResource> participantResourceList) {
        return participantResourceList.stream()
                .filter(CompetitionParticipantResource::isAccepted)
                .filter(CompetitionParticipantResource::isInAssessment)
                .map(cpr -> new AssessorDashboardActiveCompetitionViewModel(
                        cpr.getCompetitionId(),
                        cpr.getCompetitionName(),
                        cpr.getSubmittedAssessments(),
                        cpr.getTotalAssessments(),
                        cpr.getPendingAssessments(),
                        cpr.getAssessorDeadlineDate().toLocalDate(),
                        cpr.getAssessmentDaysLeft(),
                        cpr.getAssessmentDaysLeftPercentage()
                ))
                .collect(toList());
    }

    private List<AssessorDashboardUpcomingCompetitionViewModel> getUpcomingCompetitions(List<CompetitionParticipantResource> participantResources) {
        return participantResources.stream()
                .filter(CompetitionParticipantResource::isAccepted)
                .filter(CompetitionParticipantResource::isAnUpcomingAssessment)
                .map(p -> new AssessorDashboardUpcomingCompetitionViewModel(
                        p.getCompetitionId(),
                        p.getCompetitionName(),
                        p.getAssessorAcceptsDate().toLocalDate(),
                        p.getAssessorDeadlineDate().toLocalDate()
                ))
                .collect(toList());
    }

    private List<AssessorDashboardPendingInviteViewModel> getPendingParticipations(List<CompetitionParticipantResource> participantResourceList) {
        return participantResourceList.stream()
                .filter(CompetitionParticipantResource::isPending)
                .map(cpr -> new AssessorDashboardPendingInviteViewModel(
                        cpr.getInvite().getHash(),
                        cpr.getCompetitionName(),
                        cpr.getAssessorAcceptsDate().toLocalDate(),
                        cpr.getAssessorDeadlineDate().toLocalDate()
                ))
                .collect(toList());
    }

    private List<AssessorDashboardAssessmentPanelInviteViewModel> getAssessmentPanelInvites(List<ReviewParticipantResource> reviewParticipantResourceList) {
        return reviewParticipantResourceList.stream()
                .filter(ReviewParticipantResource::isPending)
                .filter(appr -> !isAfterPanelDate(appr.getCompetitionId()))
                .map(appr -> new AssessorDashboardAssessmentPanelInviteViewModel(
                        appr.getCompetitionName(),
                        appr.getCompetitionId(),
                        appr.getInvite().getHash()
                        ))
                .collect(toList());
    }

    private List<AssessorDashboardInterviewInviteViewModel> getInterviewPanelInvites(List<InterviewParticipantResource> interviewParticipantResourcesList) {
        return interviewParticipantResourcesList.stream()
                .filter(InterviewParticipantResource::isPending)
                .filter(appr -> !isAfterPanelDate(appr.getCompetitionId()))
                .map(appr -> new AssessorDashboardInterviewInviteViewModel(
                        appr.getCompetitionName(),
                        appr.getCompetitionId(),
                        appr.getInvite().getHash()
                ))
                .collect(toList());
    }

    private List<AssessorDashboardAssessmentPanelAcceptedViewModel> getAssessmentPanelAccepted(List<ReviewParticipantResource> assessmentPanelAcceptedResourceList) {
        return assessmentPanelAcceptedResourceList.stream()
                .filter(ReviewParticipantResource::isAccepted)
                .filter(appr -> !isAfterPanelDate(appr.getCompetitionId()))
                .map(appr -> new AssessorDashboardAssessmentPanelAcceptedViewModel(
                        appr.getCompetitionName(),
                        appr.getCompetitionId(),
                        appr.getInvite().getPanelDate().toLocalDate(),
                        appr.getInvite().getPanelDaysLeft(),
                        appr.getAwaitingApplications()
                        ))
                .collect(toList());
    }

    private List<AssessorDashboardInterviewAcceptedViewModel> getInterviewPanelAccepted(List<InterviewParticipantResource> interviewPanelAcceptedResourceList) {
        return interviewPanelAcceptedResourceList.stream()
                .filter(InterviewParticipantResource::isAccepted)
                .filter(appr -> !isAfterPanelDate(appr.getCompetitionId()))
                .map(appr -> new AssessorDashboardInterviewAcceptedViewModel(
                        appr.getCompetitionName(),
                        appr.getCompetitionId(),
                        appr.getAwaitingApplications()
                        ))
                .collect(toList());
    }

    private boolean isAfterPanelDate(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ZonedDateTime panelDate = competition.getFundersPanelDate();

        return ZonedDateTime.now().plusDays(1L).isAfter(panelDate);
    }
}