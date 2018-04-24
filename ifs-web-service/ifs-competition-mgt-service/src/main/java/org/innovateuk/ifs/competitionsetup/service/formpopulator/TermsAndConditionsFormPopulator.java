package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.TermsAndConditionsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Form populator for the terms and conditions competition setup section.
 */
@Service
public class TermsAndConditionsFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.TERMS_AND_CONDITIONS;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        List<TermsAndConditionsResource> termsAndConditionsList = termsAndConditionsRestService.getLatestTermsAndConditions().getSuccess();
        termsAndConditionsForm.setTermsAndConditionsList(termsAndConditionsList);

        return termsAndConditionsForm;
    }
}
