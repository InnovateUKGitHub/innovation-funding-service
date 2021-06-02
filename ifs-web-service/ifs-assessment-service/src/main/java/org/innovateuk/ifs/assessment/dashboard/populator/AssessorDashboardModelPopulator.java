package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.*;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR;

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

    private RoleProfileStatusRestService roleProfileStatusRestService;

    private AssessmentPeriodRestService assessmentPeriodRestService;

    public AssessorDashboardModelPopulator(CompetitionParticipantRestService competitionParticipantRestService,
                                           InterviewInviteRestService interviewInviteRestService,
                                           ProfileRestService profileRestService,
                                           ReviewInviteRestService reviewInviteRestService,
                                           CompetitionRestService competitionRestService,
                                           RoleProfileStatusRestService roleProfileStatusRestService,
                                           AssessmentPeriodRestService assessmentPeriodRestService) {
        this.competitionParticipantRestService = competitionParticipantRestService;
        this.interviewInviteRestService = interviewInviteRestService;
        this.profileRestService = profileRestService;
        this.reviewInviteRestService = reviewInviteRestService;
        this.competitionRestService = competitionRestService;
        this.roleProfileStatusRestService = roleProfileStatusRestService;
        this.assessmentPeriodRestService = assessmentPeriodRestService;
    }

    public AssessorDashboardViewModel populateModel(UserResource user) {

        RoleProfileState roleProfileState;
        if (user.hasRole(Role.ASSESSOR)) {
            roleProfileState = roleProfileStatusRestService.findByUserIdAndProfileRole(user.getId(), ASSESSOR).getSuccess().getRoleProfileState();
        } else {
            roleProfileState = RoleProfileState.ACTIVE;
        }

        UserProfileStatusResource profileStatusResource = profileRestService.getUserProfileStatus(user.getId()).getSuccess();

        if (roleProfileState != RoleProfileState.ACTIVE) {
            return new AssessorDashboardViewModel(getProfileStatus(profileStatusResource, roleProfileState));
        }

        List<CompetitionParticipantResource> participantResourceList = competitionParticipantRestService
                .getAssessorParticipants(user.getId()).getSuccess();

        List<CompetitionParticipantResource> participantListWithAssessmentPeriod = competitionParticipantRestService
                .getAssessorParticipantsWithAssessmentPeriod(user.getId()).getSuccess();

        List<ReviewParticipantResource> reviewParticipantResourceList = reviewInviteRestService.getAllInvitesByUser(user.getId()).getSuccess();

        List<InterviewParticipantResource> interviewParticipantResourceList = interviewInviteRestService.getAllInvitesByUser(user.getId()).getSuccess();

        return new AssessorDashboardViewModel(
                getProfileStatus(profileStatusResource, roleProfileState),
                getActiveCompetitions(participantListWithAssessmentPeriod),
                getUpcomingCompetitions(participantResourceList),
                getPendingParticipations(participantResourceList),
                getAssessmentPanelInvites(reviewParticipantResourceList),
                getAssessmentPanelAccepted(reviewParticipantResourceList),
                getInterviewPanelInvites(interviewParticipantResourceList),
                getInterviewPanelAccepted(interviewParticipantResourceList)
                );
    }

    private AssessorProfileStatusViewModel getProfileStatus(UserProfileStatusResource assessorProfileStatusResource, RoleProfileState roleProfileState) {
        return new AssessorProfileStatusViewModel(assessorProfileStatusResource, roleProfileState);
    }

    private List<AssessorDashboardActiveCompetitionViewModel> getActiveCompetitions(List<CompetitionParticipantResource> participantListWithAssessmentPeriod) {
        return participantListWithAssessmentPeriod.stream()
                .filter(CompetitionParticipantResource::isAccepted)
                .filter(competitionParticipant -> isInAssessment(competitionParticipant))
                .map(cpr -> new AssessorDashboardActiveCompetitionViewModel(
                        cpr.getCompetitionId(),
                        cpr.getCompetitionName(),
                        cpr.getSubmittedAssessments(),
                        cpr.getTotalAssessments(),
                        cpr.getPendingAssessments(),
                        cpr.getAssessorDeadlineDate().toLocalDate(),
                        cpr.getAssessmentDaysLeft(),
                        cpr.getAssessmentDaysLeftPercentage(),
                        cpr.isCompetitionAlwaysOpen(),
                        cpr.getAssessmentPeriodNumber()
                ))
                .collect(toList());
    }

    private boolean isInAssessment(CompetitionParticipantResource competitionParticipant) {
        return competitionParticipant.getAssessmentPeriod().isInAssessment();
    }

    private List<AssessorDashboardUpcomingCompetitionViewModel> getUpcomingCompetitions(List<CompetitionParticipantResource> participantListWithAssessmentPeriod) {
        return participantListWithAssessmentPeriod.stream()
                .filter(CompetitionParticipantResource::isAccepted)
                .filter(competitionParticipant -> isAnUpcomingAssessment(competitionParticipant))
                .map(p -> new AssessorDashboardUpcomingCompetitionViewModel(
                        p.getCompetitionId(),
                        p.getCompetitionName(),
                        p.getAssessorAcceptsDate() != null ? p.getAssessorAcceptsDate().toLocalDate() : null,
                        p.getAssessorDeadlineDate() != null ? p.getAssessorDeadlineDate().toLocalDate() : null,
                        p.isCompetitionAlwaysOpen()
                ))
                .collect(toList());
    }

    private boolean isAnUpcomingAssessment(CompetitionParticipantResource competitionParticipant) {
        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionParticipant.getCompetitionId()).getSuccess();
        return assessmentPeriods.stream()
                .allMatch(assessmentPeriodResource -> !assessmentPeriodResource.isInAssessment());
    }

    private List<AssessorDashboardPendingInviteViewModel> getPendingParticipations(List<CompetitionParticipantResource> participantResourceList) {
        return participantResourceList.stream()
                .filter(CompetitionParticipantResource::isPending)
                .map(cpr -> new AssessorDashboardPendingInviteViewModel(
                        cpr.getInvite().getHash(),
                        cpr.getCompetitionName(),
                        cpr.getAssessorAcceptsDate() != null ? cpr.getAssessorAcceptsDate().toLocalDate() : null,
                        cpr.getAssessorDeadlineDate() != null ? cpr.getAssessorDeadlineDate().toLocalDate() : null,
                        cpr.isCompetitionAlwaysOpen()
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

        return ZonedDateTime.now().isAfter(panelDate);
    }
}