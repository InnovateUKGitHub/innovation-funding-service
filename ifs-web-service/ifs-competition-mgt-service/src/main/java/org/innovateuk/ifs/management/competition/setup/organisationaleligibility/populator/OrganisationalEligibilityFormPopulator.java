package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Completion Stage form in Competition Setup.
 */
@Service
public class OrganisationalEligibilityFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        OrganisationalEligibilityForm organisationalEligibilityForm = new OrganisationalEligibilityForm();
        organisationalEligibilityForm.setInternationalOrganisationsApplicable(competitionResource.getInternationalOrganisationsAllowed());

        return organisationalEligibilityForm;
    }
}