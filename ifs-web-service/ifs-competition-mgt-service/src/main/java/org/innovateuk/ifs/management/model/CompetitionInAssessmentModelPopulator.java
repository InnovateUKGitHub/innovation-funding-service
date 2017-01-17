package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.CompetitionInAssessmentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition 'In Assessment' view.
 */
@Component
public class CompetitionInAssessmentModelPopulator {

    @Autowired
    private AssessmentRestService assessmentRestService;

    public CompetitionInAssessmentViewModel populateModel(CompetitionResource competition) {
        Integer changesSinceLastNotify = assessmentRestService.getByStateAndCompetition(AssessmentStates.CREATED, competition.getId()).getSuccessObject().size();
        return new CompetitionInAssessmentViewModel(competition.getId(), competition.getName(), changesSinceLastNotify);
    }
}
