package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardPendingInviteViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardUpcomingCompetitionViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardViewModel;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private UserRestService userRestService;

    public AssessorDashboardViewModel populateModel(Long userId) {
        List<CompetitionParticipantResource> participantResourceList = competitionParticipantRestService
                .getParticipants(userId, CompetitionParticipantRoleResource.ASSESSOR).getSuccessObject();

        UserProfileStatusResource profileStatusResource = userRestService.getUserProfileStatus(userId).getSuccessObject();

        return new AssessorDashboardViewModel(
                getProfileStatus(profileStatusResource),
                getActiveCompetitions(participantResourceList),
                getUpcomingCompetitions(participantResourceList),
                getPendingParticipations(participantResourceList)
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
}
