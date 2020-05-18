package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.internationalorganisation.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.internationalorganisation.form.OrganisationalEligibilityForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganisationalEligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        OrganisationalEligibilityForm organisationalEligibilityForm = (OrganisationalEligibilityForm) competitionSetupForm;

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalOrganisationsAllowed(organisationalEligibilityForm.getInternationalOrganisationsApplicable());

        return competitionOrganisationConfigRestService.update(competition.getId(), competitionOrganisationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return OrganisationalEligibilityForm.class.equals(clazz);
    }
}
