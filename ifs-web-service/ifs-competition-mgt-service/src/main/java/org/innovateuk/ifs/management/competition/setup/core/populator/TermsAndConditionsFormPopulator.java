package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.springframework.stereotype.Service;

/**
 * Form populator for the terms and conditions competition setup section.
 */
@Service
public class TermsAndConditionsFormPopulator {

    public TermsAndConditionsForm populateForm(CompetitionResource competitionResource) {
        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        if (competitionResource.getTermsAndConditions() != null) {
            termsAndConditionsForm.setTermsAndConditionsId(competitionResource.getTermsAndConditions().getId());
        }
        return termsAndConditionsForm;
    }

    public TermsAndConditionsForm populateFormForStateAid(CompetitionResource competitionResource) {
        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        if (competitionResource.getOtherFundingRulesTermsAndConditions() != null) {
            termsAndConditionsForm.setTermsAndConditionsId(competitionResource.getOtherFundingRulesTermsAndConditions().getId());
        }
        return termsAndConditionsForm;
    }
}
