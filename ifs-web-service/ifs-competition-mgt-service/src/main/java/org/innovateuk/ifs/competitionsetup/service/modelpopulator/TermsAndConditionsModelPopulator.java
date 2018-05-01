package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Populates the model for the terms and condition competition setup section.
 */
@Service
public class TermsAndConditionsModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.TERMS_AND_CONDITIONS;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel,
                                                   CompetitionResource competitionResource) {

        List<TermsAndConditionsResource> termsAndConditionsList = termsAndConditionsRestService.getLatestTermsAndConditions().getSuccess();

        TermsAndConditionsResource termsAndConditions = termsAndConditionsRestService.getById(
                competitionResource.getTermsAndConditions().getId()).getSuccess();

        return new TermsAndConditionsViewModel(generalViewModel, termsAndConditionsList, termsAndConditions);
    }
}
