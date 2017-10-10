package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.INNOVATION_LEAD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InitialDetailsSectionSaverTest {

    private static final Long COMPETITION_ID = 24L;

    @InjectMocks
    private InitialDetailsSectionSaver service;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Mock
    private UserService userService;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    //TODO INFUND-9493: Create tests for situations surrounding Milestone saving
    //TODO INFUND-9493: Create test for invalid date handling
    //TODO INFUND-9493: Create test for situations surrounding retrieval of innovation sector

    @Before
    public void setup(){
        when(competitionSetupService.isInitialDetailsComplete(COMPETITION_ID)).thenReturn(false);
    }

    @Test
    public void saveCompetitionSetupSection() {
        Long executiveUserId = 1L;
        Long competitionTypeId = 2L;
        Long leadTechnologistId = 3L;
        Long innovationAreaId = 4L;
        Long innovationSectorId = 5L;

        ZonedDateTime openingDate = ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, TimeZoneUtil.UK_TIME_ZONE);

        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(executiveUserId);
        competitionSetupForm.setOpeningDateDay(openingDate.getDayOfMonth());
        competitionSetupForm.setOpeningDateMonth(openingDate.getMonthValue());
        competitionSetupForm.setOpeningDateYear(openingDate.getYear());
        competitionSetupForm.setInnovationLeadUserId(leadTechnologistId);
        competitionSetupForm.setCompetitionTypeId(competitionTypeId);
        competitionSetupForm.setInnovationSectorCategoryId(innovationSectorId);

        InnovationAreaResource innovationArea = newInnovationAreaResource().withId(innovationAreaId).build();
        competitionSetupForm.setInnovationAreaCategoryIds(asList(innovationAreaId));

        List<MilestoneResource> milestones = getMilestoneList();

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);
        competition.setSetupComplete(false);

        when(milestoneRestService.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(restSuccess(milestones));
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(asList(innovationArea)));
        when(categoryRestService.getInnovationAreasBySector(innovationSectorId)).thenReturn(restSuccess(singletonList(innovationArea)));
        when(competitionSetupRestService.initApplicationForm(competition.getId(), competitionSetupForm.getCompetitionTypeId())).thenReturn(restSuccess());
        when(competitionSetupRestService.updateCompetitionInitialDetails(competition)).thenReturn(restSuccess());
        when(competitionSetupMilestoneService.createMilestonesForCompetition(anyLong())).thenReturn(serviceSuccess(milestones));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());
        when(userService.existsAndHasRole(executiveUserId, COMP_ADMIN)).thenReturn(true);
        when(userService.existsAndHasRole(leadTechnologistId, INNOVATION_LEAD)).thenReturn(true);

        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(competition.getExecutive(), executiveUserId);
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(competition.getLeadTechnologist(), leadTechnologistId);
        // We don't care about the order of the innovation area ids, so compare as a set
        Set<Long> expectedInnovationAreaIds = asSet(innovationAreaId);
        Set<Long> actualInnovationAreaIds = competition.getInnovationAreas().stream().collect(Collectors.toSet());
        assertEquals(expectedInnovationAreaIds, actualInnovationAreaIds);
        assertEquals(competition.getInnovationSector(), innovationSectorId);
        assertEquals(openingDate, competition.getStartDate());
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(innovationSectorId, competition.getInnovationSector());

        verify(competitionSetupRestService).updateCompetitionInitialDetails(competition);
        verify(competitionSetupRestService).initApplicationForm(competition.getId(), competitionSetupForm.getCompetitionTypeId());
        verify(userService).existsAndHasRole(executiveUserId, COMP_ADMIN);
        verify(userService).existsAndHasRole(leadTechnologistId, INNOVATION_LEAD);
    }

    @Test
    public void autoSaveCompetitionSetupSection() {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();
        competition.setMilestones(singletonList(10L));
        when(milestoneRestService.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(restSuccess(getMilestoneList()));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(competitionSetupMilestoneService.createMilestonesForCompetition(anyLong())).thenReturn(serviceSuccess(getMilestoneList()));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.autoSaveSectionField(competition, null, "openingDate", "20-10-" + (ZonedDateTime.now().getYear() + 1), null);

        assertTrue(result.isSuccess());
        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void autoSaveInnovationAreaCategoryIds() {

        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();
        competition.setInnovationAreas(Collections.singleton(999L));

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        ServiceResult<Void> errors = service.autoSaveSectionField(competition, null, "autosaveInnovationAreaIds", "1,2, 3", null);

        assertTrue(errors.isSuccess());
        assertThat(competition.getInnovationAreas(), hasItems(1L, 2L, 3L));
        assertThat(competition.getInnovationAreas(), hasSize(3));
        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void autoSaveCompetitionSetupSectionUnknown() {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();

        ServiceResult<Void> errors = service.autoSaveSectionField(competition, null, "notExisting", "Strange!@#1Value", null);

        assertTrue(!errors.isSuccess());
        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void testSaveSectionCompletedCompetitionOnlyLeadTechnologistAndExecutiveCanBeSet() {
        String newTitle = "New title";
        Long newExec = 1L;
        Long leadTechnologistId = 2L;
        Long competitionTypeId = 3L;
        Long innovationSectorId = 4L;

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withSetupComplete(true)
                .withStartDate(yesterday)
                .withFundersPanelDate(tomorrow)
                .build();

        InitialDetailsForm form = new InitialDetailsForm();
        form.setTitle(newTitle);
        form.setExecutiveUserId(newExec);
        form.setInnovationLeadUserId(leadTechnologistId);
        form.setCompetitionTypeId(competitionTypeId);
        form.setInnovationSectorCategoryId(innovationSectorId);

        when(userService.existsAndHasRole(newExec, COMP_ADMIN)).thenReturn(true);
        when(userService.existsAndHasRole(leadTechnologistId, INNOVATION_LEAD)).thenReturn(true);
        when(competitionSetupRestService.updateCompetitionInitialDetails(competition)).thenReturn(restSuccess());
        when(competitionSetupRestService.initApplicationForm(anyLong(), anyLong())).thenReturn(restSuccess());

        service.saveSection(competition, form);

        assertNull(competition.getName());
        assertEquals(competition.getLeadTechnologist(), leadTechnologistId);
        assertEquals(competition.getExecutive(), newExec);
        assertNull(competition.getCompetitionType());
        assertNull(competition.getInnovationSector());

        verify(userService).existsAndHasRole(newExec, COMP_ADMIN);
        verify(userService).existsAndHasRole(leadTechnologistId, INNOVATION_LEAD);
    }

    private List<MilestoneResource> getMilestoneList() {
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(10L);
        milestone.setType(MilestoneType.OPEN_DATE);
        milestone.setDate(ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        milestone.setCompetitionId(1L);
        return asList(milestone);
    }

    @Test
    public void supportsForm() {
        assertTrue(service.supportsForm(InitialDetailsForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }

    @Test
    public void testSaveSectionCompExecNotValid() {
        Long executiveUserId = 1L;
        Long competitionTypeId = 2L;
        Long leadTechnologistId = 3L;
        Long innovationAreaId = 4L;
        Long innovationSectorId = 5L;

        ZonedDateTime openingDate = ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, TimeZoneUtil.UK_TIME_ZONE);

        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(executiveUserId);
        competitionSetupForm.setOpeningDateDay(openingDate.getDayOfMonth());
        competitionSetupForm.setOpeningDateMonth(openingDate.getMonthValue());
        competitionSetupForm.setOpeningDateYear(openingDate.getYear());
        competitionSetupForm.setInnovationLeadUserId(leadTechnologistId);
        competitionSetupForm.setCompetitionTypeId(competitionTypeId);
        competitionSetupForm.setInnovationSectorCategoryId(innovationSectorId);

        competitionSetupForm.setInnovationAreaCategoryIds(asList(innovationAreaId, 1L, 2L, 3L));

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);
        competition.setSetupComplete(false);

        when(userService.existsAndHasRole(executiveUserId, COMP_ADMIN)).thenReturn(false);

        ServiceResult<Void> result = service.saveSection(competition, competitionSetupForm);

        assertTrue(result.isFailure());
        assertEquals("competition.setup.invalid.comp.exec", result.getFailure().getErrors().get(0).getErrorKey());
        assertEquals("executiveUserId", result.getFailure().getErrors().get(0).getFieldName());

        verify(userService).existsAndHasRole(executiveUserId, COMP_ADMIN);
    }

    @Test
    public void testSaveSectionCompTechnologistNotValid() {
        Long executiveUserId = 1L;
        Long competitionTypeId = 2L;
        Long leadTechnologistId = 3L;
        Long innovationAreaId = 4L;
        Long innovationSectorId = 5L;

        ZonedDateTime openingDate = ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, TimeZoneUtil.UK_TIME_ZONE);

        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(executiveUserId);
        competitionSetupForm.setOpeningDateDay(openingDate.getDayOfMonth());
        competitionSetupForm.setOpeningDateMonth(openingDate.getMonthValue());
        competitionSetupForm.setOpeningDateYear(openingDate.getYear());
        competitionSetupForm.setInnovationLeadUserId(leadTechnologistId);
        competitionSetupForm.setCompetitionTypeId(competitionTypeId);
        competitionSetupForm.setInnovationSectorCategoryId(innovationSectorId);

        competitionSetupForm.setInnovationAreaCategoryIds(asList(innovationAreaId, 1L, 2L, 3L));

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);
        competition.setSetupComplete(false);

        when(userService.existsAndHasRole(executiveUserId, COMP_ADMIN)).thenReturn(true);
        when(userService.existsAndHasRole(leadTechnologistId, INNOVATION_LEAD)).thenReturn(false);

        ServiceResult<Void> result = service.saveSection(competition, competitionSetupForm);

        assertTrue(result.isFailure());
        assertEquals("competition.setup.invalid.comp.technologist", result.getFailure().getErrors().get(0).getErrorKey());
        assertEquals("innovationLeadUserId", result.getFailure().getErrors().get(0).getFieldName());

        verify(userService).existsAndHasRole(executiveUserId, COMP_ADMIN);
        verify(userService).existsAndHasRole(leadTechnologistId, INNOVATION_LEAD);
    }

    @Test
    public void testSaveSectionWhenApplicationFormAlreadyInitialised() {
        Long executiveUserId = 1L;
        Long competitionTypeId = 2L;
        Long leadTechnologistId = 3L;
        Long innovationAreaId = 4L;
        Long innovationSectorId = 5L;

        ZonedDateTime openingDate = ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, TimeZoneUtil.UK_TIME_ZONE);

        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(executiveUserId);
        competitionSetupForm.setOpeningDateDay(openingDate.getDayOfMonth());
        competitionSetupForm.setOpeningDateMonth(openingDate.getMonthValue());
        competitionSetupForm.setOpeningDateYear(openingDate.getYear());
        competitionSetupForm.setInnovationLeadUserId(leadTechnologistId);
        competitionSetupForm.setCompetitionTypeId(competitionTypeId);
        competitionSetupForm.setInnovationSectorCategoryId(innovationSectorId);
        competitionSetupForm.setInnovationAreaCategoryIds(asList(innovationAreaId));

        InnovationAreaResource innovationArea = newInnovationAreaResource().withId(innovationAreaId).build();

        Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();
        sectionSetupStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionCode("compcode")
                .withMilestones(asList(10L))
                .withSetupComplete(false)
                .build();

        when(milestoneRestService.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(restSuccess(getMilestoneList()));
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(asList(innovationArea)));
        when(categoryRestService.getInnovationAreasBySector(innovationSectorId)).thenReturn(restSuccess(singletonList(innovationArea)));
        when(competitionSetupRestService.updateCompetitionInitialDetails(competition)).thenReturn(restFailure(new Error("Some Error", HttpStatus.BAD_REQUEST)));
        when(competitionSetupMilestoneService.createMilestonesForCompetition(anyLong())).thenReturn(serviceSuccess(getMilestoneList()));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());
        when(userService.existsAndHasRole(executiveUserId, COMP_ADMIN)).thenReturn(true);
        when(userService.existsAndHasRole(leadTechnologistId, INNOVATION_LEAD)).thenReturn(true);

        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(competition.getExecutive(), executiveUserId);
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(competition.getLeadTechnologist(), leadTechnologistId);
        Set<Long> expectedInnovationAreaIds = asSet(innovationAreaId);
        Set<Long> actualInnovationAreaIds = competition.getInnovationAreas().stream().collect(Collectors.toSet());
        assertEquals(expectedInnovationAreaIds, actualInnovationAreaIds);
        assertEquals(competition.getInnovationSector(), innovationSectorId);
        assertEquals(openingDate, competition.getStartDate());
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(innovationSectorId, competition.getInnovationSector());

        verify(competitionSetupRestService).updateCompetitionInitialDetails(competition);
        verify(competitionSetupRestService, never()).initApplicationForm(competition.getId(), competitionSetupForm.getCompetitionTypeId());
    }

}
