package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Function;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceSectionSaverTest {

    @InjectMocks
    private ApplicationFinanceSectionSaver service;

    @Mock
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.FINANCES, service.subsectionToSave());
    }

    @Test
    public void testSaveCompetitionSetupSection() {
        // Setup
        boolean isFullApplicationFinance = true;
        boolean isIncludeGrowthTable = true;
        Long competitionId = 1L;
        ApplicationFinanceForm competitionSetupForm = new ApplicationFinanceForm();
        competitionSetupForm.setIncludeGrowthTable(isIncludeGrowthTable);
        competitionSetupForm.setFullApplicationFinance(isFullApplicationFinance);
        CompetitionResource competition = newCompetitionResource().with(id(competitionId)).build();
        // Expectation
        CompetitionSetupFinanceResource csfr = newCompetitionSetupFinanceResource().
                withCompetitionId(competitionId).
                withFullApplicationFinance(isFullApplicationFinance).
                withIncludeGrowthTable(isIncludeGrowthTable).build();
        // Call the service under test
        service.saveSection(competition, competitionSetupForm);
        // Verify Expectations.
        verify(competitionSetupFinanceService).updateFinance(csfr);
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(ApplicationFinanceForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

    private <T> T match(Function<T, Boolean> function){
        return argThat(new ArgumentMatcher<T>() {
            @Override
            public boolean matches(Object argument) {
                return function.apply((T) argument);
            }
        });
    }
}
