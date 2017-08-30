package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
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

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceSectionSaverTest {

    @InjectMocks
    private ApplicationFinanceSectionSaver service;

    @Mock
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Test
    public void testSectionToSave() {
        assertEquals(CompetitionSetupSubsection.FINANCES, service.subsectionToSave());
    }

    @Test
    public void testSaveCompetitionSetupSection() {
        final boolean isFullApplicationFinance = true;
        final boolean isIncludeGrowthTable = true;
        final Long competitionId = 1L;
        final Long sectionId = 234L;
        final String fundingRules = "Funding rules for competition are fun, right?";

        SectionResource overviewFinanceSection = newSectionResource()
                .withId(sectionId)
                .withType(SectionType.OVERVIEW_FINANCES)
                .build();

        when(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.OVERVIEW_FINANCES)).thenReturn(asList(overviewFinanceSection));
        when(questionService.getQuestionsBySectionIdAndType(sectionId, QuestionType.GENERAL)).thenReturn(newQuestionResource()
                .withName("FINANCE_OVERVIEW", null)
                .withDescription("", fundingRules)
                .build(2));

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
        verify(questionService, times(1)).save(any(QuestionResource.class));
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(ApplicationFinanceForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

    private <T> T match(Function<T, Boolean> function) {
        return argThat(new ArgumentMatcher<T>() {
            @Override
            public boolean matches(Object argument) {
                return function.apply((T) argument);
            }
        });
    }
}
