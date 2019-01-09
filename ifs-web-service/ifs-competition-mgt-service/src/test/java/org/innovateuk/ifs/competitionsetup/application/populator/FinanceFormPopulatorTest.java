package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceRestService;
import org.innovateuk.ifs.competitionsetup.application.form.FinanceForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
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
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.FINANCES;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceFormPopulatorTest {

    @InjectMocks
    private FinanceFormPopulator populator;

    @Mock
    private CompetitionSetupFinanceRestService competitionSetupFinanceRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void sectionToFill() {
        assertEquals(FINANCES, populator.sectionToFill());
    }

    @Test
    public void populateForm() {
        final String fundingRules = "Funding rules for competition are fun, right?";

        SectionResource overviewFinanceSection = newSectionResource()
                .withType(SectionType.OVERVIEW_FINANCES)
                .build();

        CompetitionSetupFinanceResource competitionSetupFinanceResource = newCompetitionSetupFinanceResource()
                .withIncludeGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .withApplicationFinanceType(STANDARD)
                .withIncludeJesForm(true)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .build();

        assertTrue(competition.isFinanceType());

        when(competitionSetupFinanceRestService.getByCompetitionId(competition.getId()))
                .thenReturn(restSuccess(competitionSetupFinanceResource));
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), SectionType.OVERVIEW_FINANCES))
                .thenReturn(asList(overviewFinanceSection));
        when(questionRestService.getQuestionsBySectionIdAndType(overviewFinanceSection.getId(), QuestionType.GENERAL))
                .thenReturn(restSuccess(newQuestionResource()
                        .withName("FINANCE_OVERVIEW", null)
                        .withDescription("", fundingRules)
                        .build(2)));

        CompetitionSetupForm result = populator.populateForm(competition, Optional.empty());

        assertTrue(result instanceof FinanceForm);
        FinanceForm form = (FinanceForm) result;
        assertEquals(STANDARD, form.getApplicationFinanceType());
        assertTrue(form.getIncludeGrowthTable());
        assertTrue(form.getIncludeYourOrganisationSection());
        assertTrue(form.getIncludeJesForm());
        assertEquals(fundingRules, form.getFundingRules());
    }

    @Test
    public void populateForm_noFinances() {
        CompetitionResource competition = newCompetitionResource()
                .withNonFinanceType(true)
                .build();

        CompetitionSetupFinanceResource competitionSetupFinanceResource = newCompetitionSetupFinanceResource()
                .withIncludeGrowthTable(false)
                .withIncludeYourOrganisationSection(false)
                .withIncludeJesForm(false)
                .withApplicationFinanceType(NO_FINANCES)
                .build();

        assertTrue(competition.isNonFinanceType());

        when(competitionSetupFinanceRestService.getByCompetitionId(competition.getId()))
                .thenReturn(restSuccess(competitionSetupFinanceResource));

        CompetitionSetupForm result = populator.populateForm(competition, Optional.empty());

        assertTrue(result instanceof FinanceForm);
        FinanceForm form = (FinanceForm) result;
        assertEquals(NO_FINANCES, form.getApplicationFinanceType());
        assertFalse(form.getIncludeGrowthTable());
        assertFalse(form.getIncludeYourOrganisationSection());
        assertFalse(form.getIncludeJesForm());

        assertNull(form.getFundingRules());
    }
}
