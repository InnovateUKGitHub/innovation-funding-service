package org.innovateuk.ifs.competitionsetup.core.sectionupdater;


import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.competitionsetup.initialdetail.form.InitialDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermsAndConditionsSectionSaverTest {

    @InjectMocks
    private TermsAndConditionsSectionSaver saver;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void testSaveCompetitionSetupSection() {
        TermsAndConditionsResource termsAndConditionsOld = newTermsAndConditionsResource()
                .withName("default")
                .withTemplate("default-template")
                .withVersion("1").build();

        TermsAndConditionsResource termsAndConditionsNew = newTermsAndConditionsResource()
                .withName("default-new")
                .withTemplate("default-template-new")
                .withVersion("2").build();

        CompetitionResource competition = newCompetitionResource().withTermsAndConditions(termsAndConditionsOld).build();

        assertEquals("default", competition.getTermsAndConditions().getName());
        assertEquals("default-template", competition.getTermsAndConditions().getTemplate());
        assertEquals("1", competition.getTermsAndConditions().getVersion());


        TermsAndConditionsForm competitionSetupForm = new TermsAndConditionsForm();
        competitionSetupForm.setTermsAndConditionsId(termsAndConditionsNew.getId());


        when(competitionRestService.updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditionsNew.getId()))
                .thenReturn(restSuccess());

        saver.saveSection(competition, competitionSetupForm);
        verify(competitionRestService).updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditionsNew.getId());
    }

    @Test
    public void supportsForm() {
        assertTrue(saver.supportsForm(TermsAndConditionsForm.class));
        assertFalse(saver.supportsForm(InitialDetailsForm.class));
    }

}
