package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
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

    //TODO: Create tests for situations surrounding Milestone saving
    //TODO: Create test for invalid date handling
    //TODO: Create test for situations surrounding retrieval of innovation sector

    @Test
    public void testSaveCompetitionSetupSection() {
        Long executiveUserId = 1L;
        Long competitionTypeId = 2L;
        Long leadTechnologistId = 3L;
        Long innovationAreaId = 4L;
        Long innovationSectorId = 5L;

        ZonedDateTime openingDate = ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, ZoneId.systemDefault());

        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(executiveUserId);
        competitionSetupForm.setOpeningDateDay(openingDate.getDayOfMonth());
        competitionSetupForm.setOpeningDateMonth(openingDate.getMonthValue());
        competitionSetupForm.setOpeningDateYear(openingDate.getYear());
        competitionSetupForm.setLeadTechnologistUserId(leadTechnologistId);
        competitionSetupForm.setCompetitionTypeId(competitionTypeId);
        competitionSetupForm.setInnovationSectorCategoryId(innovationSectorId);

        InnovationAreaResource innovationArea = newInnovationAreaResource().withId(innovationAreaId).build();
        competitionSetupForm.setInnovationAreaCategoryIds(Arrays.asList(innovationAreaId, 1L, 2L, 3L));

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(getMilestone());

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);
        competition.setSetupComplete(false);

        when(milestoneService.getAllMilestonesByCompetitionId(1L)).thenReturn(milestones);
        when(categoryService.getInnovationAreasBySector(innovationSectorId)).thenReturn(Lists.newArrayList(innovationArea));
        when(competitionService.initApplicationFormByCompetitionType(competition.getId(), competitionSetupForm.getCompetitionTypeId())).thenReturn(serviceSuccess());
        when(competitionService.update(competition)).thenReturn(serviceSuccess());
        when(competitionSetupMilestoneService.createMilestonesForCompetition(anyLong())).thenReturn(serviceSuccess(milestones));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());

        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(competition.getExecutive(), executiveUserId);
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(competition.getLeadTechnologist(), leadTechnologistId);
        // We don't care about the order of the innovation area ids, so compare as a set
        Set<Long> expectedInnovationAreaIds = CollectionFunctions.asLinkedSet(innovationAreaId, 1L, 2L, 3L);
        Set<Long> actualInnovationAreaIds = competition.getInnovationAreas().stream().collect(Collectors.toSet());
        assertEquals(expectedInnovationAreaIds, actualInnovationAreaIds);
        assertEquals(competition.getInnovationSector(), innovationSectorId);
        assertEquals(openingDate, competition.getStartDate());
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(innovationSectorId, competition.getInnovationSector());

        verify(competitionService).update(competition);
        verify(competitionService).initApplicationFormByCompetitionType(competition.getId(), competitionSetupForm.getCompetitionTypeId());
    }

    @Test
    public void testAutoSaveCompetitionSetupSection() {
        when(milestoneService.getAllMilestonesByCompetitionId(1L)).thenReturn(asList(getMilestone()));

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(asList(10L));
        when(competitionService.update(competition)).thenReturn(serviceSuccess());
        when(competitionSetupMilestoneService.createMilestonesForCompetition(anyLong())).thenReturn(serviceSuccess(asList(getMilestone())));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.autoSaveSectionField(competition, null, "openingDate", "20-10-" + (ZonedDateTime.now().getYear() + 1), null);

        assertTrue(result.isSuccess());
        verify(competitionService).update(competition);
    }

    @Test
    public void testAutoSaveInnovationAreaCategoryIds() {

        CompetitionResource competition = newCompetitionResource().build();
        competition.setInnovationAreas(Collections.singleton(999L));

        when(competitionService.update(competition)).thenReturn(serviceSuccess());

        ServiceResult<Void> errors = service.autoSaveSectionField(competition, null, "autosaveInnovationAreaIds", "1,2, 3", null);

        assertTrue(errors.isSuccess());
        assertThat(competition.getInnovationAreas(), hasItems(1L, 2L, 3L));
        assertThat(competition.getInnovationAreas(), hasSize(3));
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
    public void testCompletedCompetitionCanSetOnlyLeadTechnologistAndExecutive() {
        String newTitle = "New title";
        Long newExec = 1L;
        Long leadTechnologistId = 2L;
        Long competitionTypeId = 3L;
        Long innovationSectorId = 4L;

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

        CompetitionResource competition = newCompetitionResource()
                .withSetupComplete(true)
                .withStartDate(yesterday)
                .withFundersPanelDate(tomorrow)
                .build();

        InitialDetailsForm form = new InitialDetailsForm();
        form.setTitle(newTitle);
        form.setExecutiveUserId(newExec);
        form.setLeadTechnologistUserId(leadTechnologistId);
        form.setCompetitionTypeId(competitionTypeId);
        form.setInnovationSectorCategoryId(innovationSectorId);

        when(competitionService.update(competition)).thenReturn(serviceSuccess());

        service.saveSection(competition, form);

        assertNull(competition.getName());
        assertEquals(competition.getLeadTechnologist(), leadTechnologistId);
        assertEquals(competition.getExecutive(), newExec);
        assertNull(competition.getCompetitionType());
        assertNull(competition.getInnovationSector());
    }

    private MilestoneResource getMilestone(){
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(10L);
        milestone.setType(MilestoneType.OPEN_DATE);
        milestone.setDate(ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        milestone.setCompetitionId(1L);
        return milestone;
    }

    @Test
    public void testsSupportsForm() {
        assertTrue(service.supportsForm(InitialDetailsForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}
