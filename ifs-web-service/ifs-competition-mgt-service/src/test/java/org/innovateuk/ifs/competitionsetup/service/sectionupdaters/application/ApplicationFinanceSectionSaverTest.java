package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Function;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceSectionSaverTest {

    @InjectMocks
    private ApplicationFinanceSectionSaver service;

    @Mock
    private CompetitionService competitionService;

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
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").
                        build();
        // Expectation
        CompetitionSetupFinanceResource csfr = newCompetitionSetupFinanceResource().
                withCompetitionId(competitionId).
                withFullApplicationFinance(isFullApplicationFinance).
                withIncludeGrowthTable(isIncludeGrowthTable).build();
        // Call the service under test
        service.saveSection(competition, competitionSetupForm);
        // Verify Expectations.
        verify(competitionSetupFinanceService, times(1)).updateFinance(csfr);
        // TODO This expectation will eventually be obsoleted - see ApplicationFinanceSectionSaver.
        verify(competitionService, times(1)).update(match(arg -> {
            return arg.isFullApplicationFinance() == isFullApplicationFinance &&
                    arg.isIncludeGrowthTable() == isIncludeGrowthTable;
        }));
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
