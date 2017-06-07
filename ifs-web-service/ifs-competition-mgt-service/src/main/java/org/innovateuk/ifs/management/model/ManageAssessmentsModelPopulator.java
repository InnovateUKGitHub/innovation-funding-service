package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ManageAssessmentsViewModel;
import org.springframework.stereotype.Component;

/**
 * Populates the model for the 'Manage assessments' page.
 */
@Component
public class ManageAssessmentsModelPopulator {

    public ManageAssessmentsViewModel populateModel(CompetitionResource competition) {
        return new ManageAssessmentsViewModel(competition);
    }
}
