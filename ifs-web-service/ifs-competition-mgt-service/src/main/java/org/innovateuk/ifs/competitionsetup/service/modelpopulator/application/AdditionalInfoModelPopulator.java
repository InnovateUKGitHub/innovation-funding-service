package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSectionModelPopulator;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * populates the model for the additional info form page
 */
@Service
public class AdditionalInfoModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.ADDITIONAL_INFO;
    }

    @Override
    public void populateModel(Model model, CompetitionResource competitionResource) {
        model.addAttribute("preventEdit", CompetitionSetupSection.ADDITIONAL_INFO.preventEdit(competitionResource));
        model.addAttribute("isSetupAndLive", competitionResource.isSetupAndLive());
        model.addAttribute("setupComplete", competitionResource.getSetupComplete());
    }
}
