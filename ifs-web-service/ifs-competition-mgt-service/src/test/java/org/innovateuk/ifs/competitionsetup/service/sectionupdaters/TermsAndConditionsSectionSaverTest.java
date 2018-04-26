package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;


import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.TermsAndConditionsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TermsAndConditionsSectionSaverTest {

    @InjectMocks
    private TermsAndConditionsSectionSaver saver;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void testSaveCompetitionSetupSection() {
        String tcName = "default";
        String tcTemplate = "default-template";
        String tcVersion = "1";

        TermsAndConditionsResource termsAndConditionsOld = newTermsAndConditionsResource()
                .withName(tcName)
                .withTemplate(tcTemplate)
                .withVersion(tcVersion).build();

        TermsAndConditionsResource termsAndConditionsNew = newTermsAndConditionsResource()
                .withName(tcName + "-new")
                .withTemplate(tcTemplate + "-new")
                .withVersion("2").build();

        CompetitionResource competition = newCompetitionResource().withTermsAndConditions(termsAndConditionsOld).build();

        assertEquals(tcName, competition.getTermsAndConditions().getName());
        assertEquals(tcTemplate, competition.getTermsAndConditions().getTemplate());
        assertEquals(tcVersion, competition.getTermsAndConditions().getVersion());


        TermsAndConditionsForm competitionSetupForm = new TermsAndConditionsForm();
        competitionSetupForm.setTermsAndConditions(termsAndConditionsNew);

        saver.saveSection(competition, competitionSetupForm);

        assertEquals(tcName + "-new", competition.getTermsAndConditions().getName());
        assertEquals(tcTemplate + "-new", competition.getTermsAndConditions().getTemplate());
        assertEquals("2", competition.getTermsAndConditions().getVersion());

        verify(competitionRestService).updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditionsNew.getId());
    }

    @Test
    public void supportsForm() {
        assertTrue(saver.supportsForm(TermsAndConditionsForm.class));
        assertFalse(saver.supportsForm(InitialDetailsForm.class));
    }

}
