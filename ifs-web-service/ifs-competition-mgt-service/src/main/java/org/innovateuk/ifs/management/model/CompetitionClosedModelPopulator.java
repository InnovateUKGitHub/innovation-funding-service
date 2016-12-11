package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.CompetitionClosedViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition 'Closed' view.
 */
@Component
public class CompetitionClosedModelPopulator {

    public CompetitionClosedViewModel populateModel(CompetitionResource competition) {
        return new CompetitionClosedViewModel(competition.getId(), competition.getName());
    }
}
