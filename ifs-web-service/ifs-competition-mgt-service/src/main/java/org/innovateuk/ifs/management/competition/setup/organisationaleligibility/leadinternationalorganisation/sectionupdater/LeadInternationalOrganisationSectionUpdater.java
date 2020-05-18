package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.leadinternationalorganisation.form.LeadInternationalOrganisationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.LEAD_INTERNATIONAL_ORGANISATION;

@Service
public class LeadInternationalOrganisationSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return  LEAD_INTERNATIONAL_ORGANISATION;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        LeadInternationalOrganisationForm leadInternationalOrganisationForm = (LeadInternationalOrganisationForm) competitionSetupForm;

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalOrganisationsAllowed(leadInternationalOrganisationForm.getLeadInternationalOrganisationApplicable());

        return competitionOrganisationConfigRestService.update(competition.getId(), competitionOrganisationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return LeadInternationalOrganisationForm.class.equals(clazz);
    }


}
