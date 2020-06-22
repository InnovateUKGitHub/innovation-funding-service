package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.LeadInternationalOrganisationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeadInternationalOrganisationFormPopulator {

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    public LeadInternationalOrganisationForm populateForm(CompetitionOrganisationConfigResource configResource) {

        LeadInternationalOrganisationForm leadInternationalOrganisationForm = new LeadInternationalOrganisationForm();
        leadInternationalOrganisationForm.setLeadInternationalOrganisationsApplicable(configResource.getInternationalLeadOrganisationAllowed());

        return leadInternationalOrganisationForm;
    }
}