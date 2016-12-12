package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.category.builder.CategoryResourceBuilder;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
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
        competitionSetupForm.setInnovationAreaCategoryIds(singleton(innovationArea.getId()));

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(getMilestone());

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);

        when(milestoneService.getAllMilestonesByCompetitionId(1L)).thenReturn(milestones);
        when(categoryService.getCategoryByParentId(innovationSectorId)).thenReturn(Lists.newArrayList(innovationArea));
        when(competitionService.initApplicationFormByCompetitionType(competition.getId(), competitionSetupForm.getCompetitionTypeId())).thenReturn(serviceSuccess());
        when(competitionService.update(competition)).thenReturn(serviceSuccess());

        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(Long.valueOf(1L), competition.getExecutive());
        assertEquals(Long.valueOf(2L), competition.getCompetitionType());
        assertEquals(Long.valueOf(3L), competition.getLeadTechnologist());
        assertEquals(singleton(Long.valueOf(innovationArea.getId())), competition.getInnovationAreas());     //TODO: INFUND-6479
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
