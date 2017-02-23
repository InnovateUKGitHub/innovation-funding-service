package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDetailsSectionSaverTest {

    @InjectMocks
    private ApplicationDetailsSectionSaver service;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.APPLICATION_DETAILS, service.subsectionToSave());
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(ApplicationDetailsForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}
