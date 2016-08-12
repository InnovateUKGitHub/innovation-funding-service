package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardUpcomingCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardViewModel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


/**
 * Build the model for the Assessor Dashboard view.
 */
@Component
public class AssessorDashboardModelPopulator {

    public AssessorDashboardViewModel populateModel() {
        return new AssessorDashboardViewModel(getInvitations(), getActiveCompetitions(), getUpcomingCompetitions());
    }

    private List<AssessorDashboardUpcomingCompetitionViewModel> getInvitations() {
        return new ArrayList<>();
    }

    private List<AssessorDashboardActiveCompetitionViewModel> getActiveCompetitions() {
        Long competitionId = 2L;
        String displayLabel = "Juggling Craziness";
        Integer progressAssessed = 1;
        Integer progressTotal = 2;
        LocalDate deadline = LocalDate.parse("2016-12-31");
        long daysLeft = 16L;
        long daysLeftPercentage = 20L;

        return asList(new AssessorDashboardActiveCompetitionViewModel(competitionId, displayLabel, progressAssessed, progressTotal, deadline, daysLeft, daysLeftPercentage));
    }

    private List<AssessorDashboardUpcomingCompetitionViewModel> getUpcomingCompetitions() {
        return new ArrayList<>();
    }
}