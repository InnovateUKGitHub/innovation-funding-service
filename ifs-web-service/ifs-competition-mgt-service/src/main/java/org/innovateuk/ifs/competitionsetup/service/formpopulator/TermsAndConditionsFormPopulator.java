package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.TermsAndConditionsForm;
import org.springframework.stereotype.Service;

/**
 * Form populator for the terms and conditions competition setup section.
 */
@Service
public class TermsAndConditionsFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.TERMS_AND_CONDITIONS;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        termsAndConditionsForm.setTermsAndConditions(competitionResource.getTermsAndConditions());
        return termsAndConditionsForm;
    }
}
