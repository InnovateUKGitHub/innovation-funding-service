package com.worth.ifs.management.model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.viewmodel.CompetitionInAssessmentViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition 'Closed' view.
 */
@Component
public class CompetitionClosedModelPopulator {

    public CompetitionInAssessmentViewModel populateModel(CompetitionResource competition) {
        return new CompetitionInAssessmentViewModel(competition.getId(), competition.getName());
    }
}