package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.*;
import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Assessor Dashboard view.
 */
@Component
public class AssessorDashboardModelPopulator {

    @Autowired
    private CompetitionParticipantRestService competitionParticipantRestService;

    @Autowired
    private ProfileRestService profileRestService;

    @Autowired
    private AssessmentPanelInviteRestService assessmentPanelInviteRestService;

    public AssessorDashboardViewModel populateModel(Long userId) {
        List<CompetitionParticipantResource> participantResourceList = competitionParticipantRestService
                .getParticipants(userId, CompetitionParticipantRoleResource.ASSESSOR).getSuccessObject();

        UserProfileStatusResource profileStatusResource = profileRestService.getUserProfileStatus(userId).getSuccessObject();

        List<AssessmentPanelInviteResource> assessmentPanelInviteResources = assessmentPanelInviteRestService.getAllInvitesByUser(userId).getSuccessObject();

        return new AssessorDashboardViewModel(
                getProfileStatus(profileStatusResource),
                getActiveCompetitions(participantResourceList),
                getUpcomingCompetitions(participantResourceList),
                getPendingParticipations(participantResourceList),
                getAssessmentPanelInvites(assessmentPanelInviteResources)
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

    private List<AssessorDashboardAssessmentPanelInviteViewModel> getAssessmentPanelInvites(List<AssessmentPanelInviteResource> assessmentPanelPendingInviteResources) {
        return assessmentPanelPendingInviteResources.stream()
                .filter(AssessmentPanelInviteResource::isPending)
                .map(invite -> new AssessorDashboardAssessmentPanelInviteViewModel(
                        invite.getHash(),
                        invite.getCompetitionName(),
                        invite.getCompetitionId()
                ))
                .collect(toList());
    }
}
