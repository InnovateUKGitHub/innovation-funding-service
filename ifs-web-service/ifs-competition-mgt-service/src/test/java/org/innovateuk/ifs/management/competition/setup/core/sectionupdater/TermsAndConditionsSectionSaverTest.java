package org.innovateuk.ifs.management.competition.setup.core.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TermsAndConditionsSectionSaverTest {

    @InjectMocks
    private TermsAndConditionsSectionSaver saver;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void testSaveCompetitionSetupSection() {
        GrantTermsAndConditionsResource termsAndConditionsOld = newGrantTermsAndConditionsResource()
                .withName("default")
                .withTemplate("default-template")
                .withVersion(1).build();

        GrantTermsAndConditionsResource termsAndConditionsNew = newGrantTermsAndConditionsResource()
                .withName("default-new")
                .withTemplate("default-template-new")
                .withVersion(2).build();

        CompetitionResource competition = newCompetitionResource().withTermsAndConditions(termsAndConditionsOld)
                .build();

        assertEquals("default", competition.getTermsAndConditions().getName());
        assertEquals("default-template", competition.getTermsAndConditions().getTemplate());
        assertEquals(1, competition.getTermsAndConditions().getVersion());


        TermsAndConditionsForm competitionSetupForm = new TermsAndConditionsForm();
        competitionSetupForm.setTermsAndConditionsId(termsAndConditionsNew.getId());


        when(competitionRestService.updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditionsNew
                .getId()))
                .thenReturn(restSuccess());

        saver.saveSection(competition, competitionSetupForm);
        verify(competitionRestService).updateTermsAndConditionsForCompetition(competition.getId(),
                termsAndConditionsNew.getId());
    }

    @Test
    public void supportsForm() {
        assertTrue(saver.supportsForm(TermsAndConditionsForm.class));
        assertFalse(saver.supportsForm(InitialDetailsForm.class));
    }

}
