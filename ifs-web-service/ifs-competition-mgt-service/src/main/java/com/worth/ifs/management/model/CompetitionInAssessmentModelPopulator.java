package com.worth.ifs.management.model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.viewmodel.CompetitionInAssessmentViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition 'In Assessment' view.
 */
@Component
public class CompetitionInAssessmentModelPopulator {

    public CompetitionInAssessmentViewModel populateModel(CompetitionResource competition) {
        return new CompetitionInAssessmentViewModel(competition.getId(), competition.getName());
    }
}