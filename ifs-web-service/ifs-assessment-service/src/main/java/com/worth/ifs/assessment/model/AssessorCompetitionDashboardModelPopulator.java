package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardViewModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Build the model for the Assessor Competition Dashboard view.
 */
@Component
public class AssessorCompetitionDashboardModelPopulator {

    public AssessorCompetitionDashboardViewModel populateModel(Long competitionId) {
        String competitionTitle = "Juggling Craziness";
        String competition = "Juggling Craziness (CRD3359)";
        String fundingBody = "Innovate UK";

        return new AssessorCompetitionDashboardViewModel(competitionTitle, competition, fundingBody, getApplications());
    }

    private List<AssessorCompetitionDashboardApplicationViewModel> getApplications() {
        return asList(getAssessment1(), getAssessment2());
    }

    private AssessorCompetitionDashboardApplicationViewModel getAssessment1() {
        Long applicationId = 8L;
        Long assessmentId = 9L;
        String displayLabel = "Juggling is fun";
        String leadOrganisation = "The Best Juggling Company";

        return new AssessorCompetitionDashboardApplicationViewModel(applicationId, assessmentId, displayLabel, leadOrganisation);
    }

    private AssessorCompetitionDashboardApplicationViewModel getAssessment2() {
        Long applicationId = 14L;
        Long assessmentId = 10L;
        String displayLabel = "Juggling is word that sounds funny to say";
        String leadOrganisation = "Mo Juggling Mo Problems Ltd";

        return new AssessorCompetitionDashboardApplicationViewModel(applicationId, assessmentId, displayLabel, leadOrganisation);
    }
}