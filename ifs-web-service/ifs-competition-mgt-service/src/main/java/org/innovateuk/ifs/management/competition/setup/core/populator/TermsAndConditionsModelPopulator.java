package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.TermsAndConditionsViewModel;
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

        List<GrantTermsAndConditionsResource> termsAndConditionsList = termsAndConditionsRestService
                .getLatestVersionsForAllTermsAndConditions()
                .getSuccess();

        GrantTermsAndConditionsResource termsAndConditions = termsAndConditionsRestService.getById(
                competitionResource.getTermsAndConditions().getId()).getSuccess();

        // TODO failing due to mapstruct populating the field?
//        boolean termsAndConditionsDocUploaded = competitionResource.getCompetitionTerms() != null;
        boolean termsAndConditionsDocUploaded = false;

        return new TermsAndConditionsViewModel(generalViewModel, termsAndConditionsList,
                termsAndConditions, termsAndConditionsDocUploaded);
    }
}