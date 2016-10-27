package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.service.CompetitionParticipantRestService;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardUpcomingCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardViewModel;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileStatusViewModel;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.user.resource.UserProfileStatusResource;
import com.worth.ifs.user.service.UserRestService;
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
                .getParticipants(userId, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED).getSuccessObject();



        UserProfileStatusResource profileStatusResource = userRestService.getUserProfileStatus(userId).getSuccessObject();

        return new AssessorDashboardViewModel(getProfileStatus(profileStatusResource), getActiveCompetitions(participantResourceList), getUpcomingCompetitions(participantResourceList));
    }

    private AssessorProfileStatusViewModel getProfileStatus(UserProfileStatusResource assessorProfileStatusResource) {
        return new AssessorProfileStatusViewModel(
                assessorProfileStatusResource.isSkillsComplete(),
                assessorProfileStatusResource.isAffiliationsComplete(),
                assessorProfileStatusResource.isContractComplete()
        );
    }

    private List<AssessorDashboardActiveCompetitionViewModel> getActiveCompetitions(List<CompetitionParticipantResource> participantResourceList) {
        return participantResourceList.stream()
                .filter(CompetitionParticipantResource::isInAssessment)
                .map(cpr ->
                     new AssessorDashboardActiveCompetitionViewModel(
                            cpr.getCompetitionId(),
                            cpr.getCompetitionName(),
                            1,
                            2,
                            cpr.getAssessorDeadlineDate().toLocalDate(),
                            cpr.getAssessmentDaysLeft(),
                            cpr.getAssessmentDaysLeftPercentage())
                )
                .collect(toList());
    }

    private List<AssessorDashboardUpcomingCompetitionViewModel> getUpcomingCompetitions(List<CompetitionParticipantResource> participantResources) {
        return participantResources.stream()
                .filter(CompetitionParticipantResource::isAnUpcomingAssessment)
                .map( p -> new AssessorDashboardUpcomingCompetitionViewModel(
                        p.getCompetitionId(),
                        p.getCompetitionName(),
                        p.getAssessorAcceptsDate().toLocalDate(),
                        p.getAssessorDeadlineDate().toLocalDate())
                )
                .collect(toList());
    }
}