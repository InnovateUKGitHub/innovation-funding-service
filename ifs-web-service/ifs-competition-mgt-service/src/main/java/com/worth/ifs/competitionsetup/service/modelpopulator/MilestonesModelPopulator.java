package com.worth.ifs.competitionsetup.service.modelpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import org.springframework.ui.Model;

/**
 * Populates the model for the milestones competition setup section.
 */
public class MilestonesModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    public void populateModel(Model model, CompetitionResource competitionResource) {

    }
}
