package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceFormPopulatorTest {

    @InjectMocks
    private ApplicationFinanceFormPopulator populator;

    @Mock
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Test
    public void testSectionToFill() {
        CompetitionSetupSubsection result = populator.sectionToFill();
        assertEquals(CompetitionSetupSubsection.FINANCES, result);
    }

    @Test
    public void testPopulateWithoutErrors() {
        final long compId = 8L;
        final long sectionId = 234L;
        final boolean isFullApplication = true;
        final boolean isIncludeGrowthTable = true;
        final String fundingRules = "Funding rules for competition are fun, right?";

        SectionResource overviewFinanceSection = newSectionResource()
                .withId(sectionId)
                .withType(SectionType.OVERVIEW_FINANCES)
                .build();
        CompetitionSetupFinanceResource csfr = newCompetitionSetupFinanceResource()
                .withIncludeGrowthTable(isIncludeGrowthTable)
                .withFullApplicationFinance(isFullApplication)
                .build();

        when(competitionSetupFinanceService.getByCompetitionId(compId)).thenReturn(csfr);
        when(sectionService.getSectionsForCompetitionByType(compId, SectionType.OVERVIEW_FINANCES)).thenReturn(asList(overviewFinanceSection));
        when(questionService.getQuestionsBySectionIdAndType(sectionId, QuestionType.GENERAL)).thenReturn(newQuestionResource()
                .withName("FINANCE_OVERVIEW", null)
                .withDescription("", fundingRules)
                .build(2));

        CompetitionResource competition = newCompetitionResource()
                .withId(compId)
                .build();

        CompetitionSetupForm result = populator.populateForm(competition, Optional.empty());

        assertTrue(result instanceof ApplicationFinanceForm);
        ApplicationFinanceForm form = (ApplicationFinanceForm) result;
        assertEquals(isFullApplication, form.isFullApplicationFinance());
        assertEquals(isIncludeGrowthTable, form.isIncludeGrowthTable());
        assertEquals(fundingRules, form.getFundingRules());
    }
}
