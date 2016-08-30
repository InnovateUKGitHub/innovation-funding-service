package com.worth.ifs.assessment.model;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.service.CompetitionParticipantRestService;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardUpcomingCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Build the model for the Assessor Dashboard view.
 */
@Component
public class AssessorDashboardModelPopulator {

    @Autowired
    CompetitionParticipantRestService competitionParticipantRestService;

    @Autowired
    private CompetitionService competitionService;

    private List<CompetitionResource> activeCompetitions;

    public AssessorDashboardViewModel populateModel(Long userId) {
        return new AssessorDashboardViewModel(getActiveCompetitions(userId), getUpcomingCompetitions());
    }

    private List<AssessorDashboardActiveCompetitionViewModel> getActiveCompetitions(Long userId) {
        List<CompetitionParticipantResource> participantResourceList = competitionParticipantRestService
                .getParticipants(userId, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED).getSuccessObject();

        return participantResourceList.stream()
                .map(cpr -> {
                    CompetitionResource competition = competitionService.getById(cpr.getCompetitionId());
                    return new AssessorDashboardActiveCompetitionViewModel(competition.getId(),
                            competition.getName(),
                            1,
                            2,
                            competition.getAssessmentEndDate().toLocalDate(),
                            competition.getDaysLeft(),
                            competition.getAssessmentDaysLeftPercentage());
                }).collect(Collectors.toList());
    }

    private List<AssessorDashboardUpcomingCompetitionViewModel> getUpcomingCompetitions() {
        return new ArrayList<>();
    }
}