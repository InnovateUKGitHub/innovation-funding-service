package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.common.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.application.form.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.common.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.setup.resource.ApplicationFinanceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceSectionSaverTest {

    @InjectMocks
    private ApplicationFinanceSectionUpdater service;

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
        final ApplicationFinanceType applicationFinanceType = ApplicationFinanceType.FULL;
        final boolean isIncludeGrowthTable = true;
        final boolean isFullApplicationFinance = true;
        final Long competitionId = 1L;
        final Long sectionId = 234L;
        final String fundingRules = "Funding rules for competition are fun, right?";

        SectionResource overviewFinanceSection = newSectionResource()
                .withId(sectionId)
                .withType(SectionType.OVERVIEW_FINANCES)
                .build();

        CompetitionResource competition = newCompetitionResource().with(id(competitionId)).build();

        assertTrue(competition.isFinanceType());

        when(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.OVERVIEW_FINANCES))
                .thenReturn(asList(overviewFinanceSection));
        when(questionService.getQuestionsBySectionIdAndType(sectionId, QuestionType.GENERAL))
                .thenReturn(
                        newQuestionResource()
                                .withName("FINANCE_OVERVIEW", null)
                                .withDescription("", fundingRules)
                                .build(2)
                );

        ApplicationFinanceForm competitionSetupForm = new ApplicationFinanceForm();
        competitionSetupForm.setIncludeGrowthTable(isIncludeGrowthTable);
        competitionSetupForm.setApplicationFinanceType(applicationFinanceType);

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
    public void testSaveCompetitionSetupSectionNoneFinance() {
        final ApplicationFinanceType applicationFinanceType = ApplicationFinanceType.NONE;
        final Long competitionId = 1L;

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withCompetitionTypeName(CompetitionResource.NON_FINANCE_TYPES.iterator().next())
                .build();

        assertTrue(competition.isNonFinanceType());

        ApplicationFinanceForm competitionSetupForm = new ApplicationFinanceForm();
        competitionSetupForm.setApplicationFinanceType(applicationFinanceType);

        // Call the service under test
        service.saveSection(competition, competitionSetupForm);
        // Verify Expectations.
        verify(competitionSetupFinanceService, never()).updateFinance(any());
        verify(questionService, never()).save(any(QuestionResource.class));
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
