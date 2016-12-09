package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.google.common.collect.Lists;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.category.builder.CategoryResourceBuilder;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.InitialDetailsForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InitialDetailsSectionSaverTest {

    @InjectMocks
    private InitialDetailsSectionSaver service;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private MilestoneService milestoneService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Test
    public void testSaveCompetitionSetupSection() {
        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(1L);
        competitionSetupForm.setOpeningDateDay(1);
        competitionSetupForm.setOpeningDateMonth(12);
        competitionSetupForm.setOpeningDateYear(2020);
        competitionSetupForm.setCompetitionTypeId(2L);
        competitionSetupForm.setLeadTechnologistUserId(3L);

        long innovationSectorId = 5L;
        competitionSetupForm.setInnovationSectorCategoryId(innovationSectorId);
        CategoryResource innovationArea = CategoryResourceBuilder.newCategoryResource().build();
        competitionSetupForm.setInnovationAreaCategoryId(innovationArea.getId());

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(getMilestone());

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);
        competition.setSetupComplete(false);

        when(milestoneService.getAllMilestonesByCompetitionId(1L)).thenReturn(milestones);
        when(categoryService.getCategoryByParentId(innovationSectorId)).thenReturn(Lists.newArrayList(innovationArea));
        when(competitionService.initApplicationFormByCompetitionType(competition.getId(), competitionSetupForm.getCompetitionTypeId())).thenReturn(serviceSuccess());
        when(competitionService.update(competition)).thenReturn(serviceSuccess());

        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(Long.valueOf(1L), competition.getExecutive());
        assertEquals(Long.valueOf(2L), competition.getCompetitionType());
        assertEquals(Long.valueOf(3L), competition.getLeadTechnologist());
        assertEquals(Long.valueOf(innovationArea.getId()), competition.getInnovationArea());
        assertEquals(Long.valueOf(innovationSectorId), competition.getInnovationSector());
        assertEquals(LocalDateTime.of(2020, 12, 1, 0, 0), competition.getStartDate());

        verify(competitionService).update(competition);
        verify(competitionService).initApplicationFormByCompetitionType(competition.getId(), competitionSetupForm.getCompetitionTypeId());
    }

    @Test
    public void testAutoSaveCompetitionSetupSection() {
        when(milestoneService.getAllMilestonesByCompetitionId(1L)).thenReturn(asList(getMilestone()));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        when(competitionService.update(competition)).thenReturn(serviceSuccess());

        ServiceResult<Void> errors = service.autoSaveSectionField(competition, null, "openingDate", "20-10-2020", null);

        assertTrue(errors.isSuccess());
        verify(competitionService).update(competition);
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionUnknown() {
        CompetitionResource competition = newCompetitionResource().build();

        ServiceResult<Void> errors = service.autoSaveSectionField(competition, null, "notExisting", "Strange!@#1Value", null);

        assertTrue(!errors.isSuccess());
        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testCompleteCompetitionCannotSetFields() {
        CompetitionResource competition = newCompetitionResource().build();
        competition.setSetupComplete(true);
        competition.setStartDate(LocalDateTime.now().minusDays(1));
        InitialDetailsForm form = new InitialDetailsForm();
        String newTitle = "New title";
        Long newExec = 1L;
        form.setTitle(newTitle);
        form.setExecutiveUserId(newExec);
        when(competitionService.update(competition)).thenReturn(serviceSuccess());

        service.saveSection(competition, form);

        assertThat(competition.getName(), is(not(equalTo(newTitle))));
        assertThat(competition.getExecutive(), is(equalTo(newExec)));

    }

    private MilestoneResource getMilestone(){
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(10L);
        milestone.setType(MilestoneType.OPEN_DATE);
        milestone.setDate(LocalDateTime.of(2020, 12, 1, 0, 0));
        milestone.setCompetition(1L);
        return milestone;
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(InitialDetailsForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}