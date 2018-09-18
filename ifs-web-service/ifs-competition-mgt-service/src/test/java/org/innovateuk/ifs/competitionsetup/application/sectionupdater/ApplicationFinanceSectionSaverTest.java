package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceRestService;
import org.innovateuk.ifs.competitionsetup.application.form.FinanceForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFinanceSectionSaverTest {

    @InjectMocks
    private FinanceSectionUpdater service;

    @Mock
    private CompetitionSetupFinanceRestService competitionSetupFinanceRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void subsectionToSave() {
        assertEquals(CompetitionSetupSubsection.FINANCES, service.subsectionToSave());
    }

    @Test
    public void doSaveSection() {
        final boolean isIncludeGrowthTable = true;
        final String fundingRules = "Funding rules for competition are fun, right?";

        SectionResource overviewFinanceSection = newSectionResource()
                .withType(SectionType.OVERVIEW_FINANCES)
                .build();

        CompetitionResource competition = newCompetitionResource().build();

        assertTrue(competition.isFinanceType());

        CompetitionSetupFinanceResource competitionSetupFinanceResource = newCompetitionSetupFinanceResource()
                .withCompetitionId(competition.getId())
                .withApplicationFinanceType(STANDARD)
                .withIncludeGrowthTable(isIncludeGrowthTable)
                .build();

        when(competitionSetupFinanceRestService.save(competitionSetupFinanceResource)).thenReturn(restSuccess());
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), SectionType.OVERVIEW_FINANCES))
                .thenReturn(asList(overviewFinanceSection));
        when(questionRestService.getQuestionsBySectionIdAndType(overviewFinanceSection.getId(), QuestionType.GENERAL))
                .thenReturn(
                        restSuccess(newQuestionResource()
                                .withName("FINANCE_OVERVIEW", null)
                                .withDescription("", fundingRules)
                                .build(2)
                ));

        FinanceForm competitionSetupForm = new FinanceForm();
        competitionSetupForm.setIncludeGrowthTable(isIncludeGrowthTable);
        competitionSetupForm.setApplicationFinanceType(STANDARD);

        service.saveSection(competition, competitionSetupForm);

        verify(competitionSetupFinanceRestService).save(competitionSetupFinanceResource);
        verify(questionRestService, times(1)).save(any(QuestionResource.class));
    }

    @Test
    public void doSaveSection_noFinances() {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionTypeName(CompetitionResource.NON_FINANCE_TYPES.iterator().next())
                .build();

        assertTrue(competition.isNonFinanceType());

        CompetitionSetupFinanceResource competitionSetupFinanceResource = newCompetitionSetupFinanceResource()
                .withCompetitionId(competition.getId())
                .withApplicationFinanceType(NO_FINANCES)
                .withIncludeGrowthTable(false)
                .build();

        when(competitionSetupFinanceRestService.save(competitionSetupFinanceResource)).thenReturn(restSuccess());

        FinanceForm competitionSetupForm = new FinanceForm();
        competitionSetupForm.setApplicationFinanceType(NO_FINANCES);

        service.saveSection(competition, competitionSetupForm);

        verify(competitionSetupFinanceRestService).save(competitionSetupFinanceResource);
        verify(questionRestService, never()).save(any(QuestionResource.class));
    }

    @Test
    public void supportsForm() {
        assertTrue(service.supportsForm(FinanceForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}
