package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition 'In Assessment' view.
 */
@Component
public class CompetitionInFlightModelPopulator {

    public CompetitionInFlightViewModel populateModel(CompetitionResource competition) {
        return new CompetitionInFlightViewModel(competition);
    }
}
