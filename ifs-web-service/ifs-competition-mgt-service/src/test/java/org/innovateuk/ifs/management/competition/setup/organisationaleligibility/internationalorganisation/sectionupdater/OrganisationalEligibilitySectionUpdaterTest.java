package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.internationalorganisation.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.internationalorganisation.form.OrganisationalEligibilityForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrganisationalEligibilitySectionUpdaterTest {

    @InjectMocks
    private OrganisationalEligibilitySectionUpdater updater;

    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Test
    public void doSaveSection() {
        long competitionId = 100L;
        CompetitionResource competition = newCompetitionResource().withId(competitionId).build();
        OrganisationalEligibilityForm form = new OrganisationalEligibilityForm();
        form.setInternationalOrganisationsApplicable(true);
        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(form.getInternationalOrganisationsApplicable())
                .build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));
        when(competitionOrganisationConfigRestService.update(competitionId,configResource)).thenReturn(restSuccess(configResource));

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form);

        assertTrue(updateResult.isSuccess());
        verify(competitionOrganisationConfigRestService).findByCompetitionId(competitionId);
        verify(competitionOrganisationConfigRestService).update(competitionId,configResource);
    }

    @Test
    public void supportsForm() {
        assertTrue(updater.supportsForm(OrganisationalEligibilityForm.class));
        assertFalse(updater.supportsForm(CompetitionSetupForm.class));
    }
}