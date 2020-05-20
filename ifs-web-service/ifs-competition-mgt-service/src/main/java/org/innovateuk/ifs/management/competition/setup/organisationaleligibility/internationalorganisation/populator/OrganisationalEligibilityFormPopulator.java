package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.internationalorganisation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.internationalorganisation.form.OrganisationalEligibilityForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Completion Stage form in Competition Setup.
 */
@Service
public class OrganisationalEligibilityFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionResource.getId()).getSuccess();

        OrganisationalEligibilityForm organisationalEligibilityForm = new OrganisationalEligibilityForm();
        organisationalEligibilityForm.setInternationalOrganisationsApplicable(competitionOrganisationConfigResource.getInternationalOrganisationsAllowed());
        organisationalEligibilityForm.setLeadInternationalOrganisationsApplicable(competitionOrganisationConfigResource.getInternationalLeadOrganisationAllowed());

        return organisationalEligibilityForm;
    }
}