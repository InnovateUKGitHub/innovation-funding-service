package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.form.LeadInternationalOrganisationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.LEAD_INTERNATIONAL_ORGANISATION;

@Service
public class LeadInternationalOrganisationFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return LEAD_INTERNATIONAL_ORGANISATION;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competitionResource.getId()).getSuccess();

        LeadInternationalOrganisationForm leadInternationalOrganisationForm = new LeadInternationalOrganisationForm();
        leadInternationalOrganisationForm.setLeadInternationalOrganisationApplicable(competitionOrganisationConfigResource.getInternationalLeadOrganisationAllowed());

        return leadInternationalOrganisationForm;
    }

}
