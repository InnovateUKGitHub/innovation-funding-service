package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.application.form.FinanceForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.setup.resource.ApplicationFinanceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceFormPopulatorTest {

    @InjectMocks
    private FinanceFormPopulator populator;

    @Mock
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionRestService questionRestService;

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

        CompetitionResource competition = newCompetitionResource()
                .withId(compId)
                .build();

        assertTrue(competition.isFinanceType());

        when(competitionSetupFinanceService.getByCompetitionId(compId)).thenReturn(csfr);
        when(sectionService.getSectionsForCompetitionByType(compId, SectionType.OVERVIEW_FINANCES)).thenReturn(asList(overviewFinanceSection));
        when(questionRestService.getQuestionsBySectionIdAndType(sectionId, QuestionType.GENERAL)).thenReturn(restSuccess(newQuestionResource()
                .withName("FINANCE_OVERVIEW", null)
                .withDescription("", fundingRules)
                .build(2)));

        CompetitionSetupForm result = populator.populateForm(competition, Optional.empty());

        assertTrue(result instanceof FinanceForm);
        FinanceForm form = (FinanceForm) result;
        assertEquals(ApplicationFinanceType.FULL, form.getApplicationFinanceType());
        assertEquals(isIncludeGrowthTable, form.isIncludeGrowthTable());
        assertEquals(fundingRules, form.getFundingRules());
    }

    @Test
    public void testPopulateWithoutErrorsForNonFinances() {
        final long compId = 8L;
        final long sectionId = 234L;
        final boolean isFullApplication = false;
        final boolean isIncludeGrowthTable = true;
        final String fundingRules = "Funding rules for competition are fun, right?";

        SectionResource overviewFinanceSection = newSectionResource()
                .withId(sectionId)
                .withType(SectionType.OVERVIEW_FINANCES)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withId(compId)
                .withCompetitionTypeName(CompetitionResource.NON_FINANCE_TYPES.iterator().next())
                .build();

        assertTrue(competition.isNonFinanceType());

        CompetitionSetupForm result = populator.populateForm(competition, Optional.empty());

        assertTrue(result instanceof FinanceForm);
        FinanceForm form = (FinanceForm) result;
        assertEquals(ApplicationFinanceType.NONE, form.getApplicationFinanceType());
    }
}
