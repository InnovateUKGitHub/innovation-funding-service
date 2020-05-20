package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.form.LeadInternationalOrganisationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeadInternationalOrganisationFormPopulator {

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    public LeadInternationalOrganisationForm populateForm(CompetitionResource competitionResource) {

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionResource.getId()).getSuccess();

        LeadInternationalOrganisationForm leadInternationalOrganisationForm = new LeadInternationalOrganisationForm();
        leadInternationalOrganisationForm.setLeadInternationalOrganisationsApplicable(competitionOrganisationConfigResource.getInternationalLeadOrganisationAllowed());

        return leadInternationalOrganisationForm;
    }

}
