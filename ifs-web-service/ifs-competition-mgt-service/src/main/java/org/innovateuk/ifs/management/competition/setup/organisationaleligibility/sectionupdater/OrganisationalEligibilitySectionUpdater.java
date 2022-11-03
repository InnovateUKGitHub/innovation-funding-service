package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class OrganisationalEligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Value("${ifs.impact.management.enabled}")
    private boolean isProjectImpactEnabled;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser) {

        OrganisationalEligibilityForm organisationalEligibilityForm = (OrganisationalEligibilityForm) competitionSetupForm;

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = competitionOrganisationConfigRestService.findByCompetitionId(competition.getId()).getSuccess();
        competitionOrganisationConfigResource.setInternationalOrganisationsAllowed(organisationalEligibilityForm.getInternationalOrganisationsApplicable());

        return competitionOrganisationConfigRestService.update(competition.getId(), competitionOrganisationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return OrganisationalEligibilityForm.class.equals(clazz);
    }

    @Override
    public String getNextSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competition, CompetitionSetupSection section) {

        String sectionPath;

        if (isProjectImpactEnabled) {
            sectionPath = CompetitionSetupSection.PROJECT_IMPACT.getPath();
        } else {
                sectionPath = CompetitionSetupSection.APPLICATION_FORM.getPath();
        }

        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), sectionPath);
    }
}
