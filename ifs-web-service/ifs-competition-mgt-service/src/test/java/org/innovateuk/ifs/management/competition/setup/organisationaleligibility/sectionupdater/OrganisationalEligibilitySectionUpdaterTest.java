package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionApplicationConfigResourceBuilder.newCompetitionApplicationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
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

        UserResource loggedInUser = newUserResource().build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));
        when(competitionOrganisationConfigRestService.update(competitionId,configResource)).thenReturn(restSuccess(configResource));

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form, loggedInUser);

        assertTrue(updateResult.isSuccess());
        verify(competitionOrganisationConfigRestService).findByCompetitionId(competitionId);
        verify(competitionOrganisationConfigRestService).update(competitionId,configResource);
    }

    @Test
    public void getNextSectionProjectImpactNotEnabled() {

        ReflectionTestUtils.setField(updater, "isProjectImpactEnabled", false);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(false)
                .build();
        OrganisationalEligibilityForm form = new OrganisationalEligibilityForm();

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.APPLICATION_FORM);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/application");
    }

    @Test
    public void getNextSectionProjectImpactEnabled() {

        ReflectionTestUtils.setField(updater, "isProjectImpactEnabled", true);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withCompetitionApplicationConfig(newCompetitionApplicationConfigResource()
                        .withIMSurveyRequired(true)
                        .build())
                .withAlwaysOpen(false)
                .build();
        OrganisationalEligibilityForm form = new OrganisationalEligibilityForm();

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.PROJECT_IMPACT);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/project-impact");
    }

    @Test
    public void supportsForm() {
        assertTrue(updater.supportsForm(OrganisationalEligibilityForm.class));
        assertFalse(updater.supportsForm(CompetitionSetupForm.class));
    }
}