package org.innovateuk.ifs.management.competition.setup.initialdetail.sectionupdater;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InitialDetailsSectionSaverTest {

    private static final Long COMPETITION_ID = 24L;

    @InjectMocks
    private InitialDetailsSectionUpdater service;

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

    @Before
    public void setup(){
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(false);
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
        competitionSetupForm.setStateAid(Boolean.TRUE);
        competitionSetupForm.setFundingType(FundingType.GRANT);

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
        when(categoryRestService.getInnovationAreasExcludingNone()).thenReturn(restSuccess(asList(innovationArea)));
        when(categoryRestService.getInnovationAreasBySector(innovationSectorId)).thenReturn(restSuccess(singletonList(innovationArea)));
        when(competitionSetupRestService.initApplicationForm(competition.getId(), competitionSetupForm.getCompetitionTypeId())).thenReturn(restSuccess());
        when(competitionSetupRestService.updateCompetitionInitialDetails(competition)).thenReturn(restSuccess());
        when(competitionSetupMilestoneService.createMilestonesForIFSCompetition(anyLong())).thenReturn(serviceSuccess(milestones));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());
        when(userService.existsAndHasRole(executiveUserId, COMP_ADMIN)).thenReturn(true);
        when(userService.existsAndHasRole(leadTechnologistId, INNOVATION_LEAD)).thenReturn(true);
        when(milestoneRestService.updateMilestone(any(MilestoneResource.class))).thenReturn(restSuccess());
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(any(), any())).thenReturn(restFailure(new Error("No milestone", HttpStatus.BAD_REQUEST)));

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
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(innovationSectorId, competition.getInnovationSector());
        assertEquals(Boolean.TRUE, competition.getStateAid());
        assertEquals(FundingType.GRANT, competition.getFundingType());

        verify(competitionSetupRestService).updateCompetitionInitialDetails(competition);
        verify(competitionSetupRestService).initApplicationForm(competition.getId(), competitionSetupForm.getCompetitionTypeId());
        verify(userService).existsAndHasRole(executiveUserId, COMP_ADMIN);
        verify(userService).existsAndHasRole(leadTechnologistId, INNOVATION_LEAD);
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
        when(categoryRestService.getInnovationAreasExcludingNone()).thenReturn(restSuccess(asList(innovationArea)));
        when(categoryRestService.getInnovationAreasBySector(innovationSectorId)).thenReturn(restSuccess(singletonList(innovationArea)));
        when(competitionSetupRestService.updateCompetitionInitialDetails(competition)).thenReturn(restFailure(new Error("Some Error", HttpStatus.BAD_REQUEST)));
        when(competitionSetupMilestoneService.createMilestonesForIFSCompetition(anyLong())).thenReturn(serviceSuccess(getMilestoneList()));
        when(competitionSetupMilestoneService.updateMilestonesForCompetition(anyList(), anyMap(), anyLong())).thenReturn(serviceSuccess());
        when(userService.existsAndHasRole(executiveUserId, COMP_ADMIN)).thenReturn(true);
        when(userService.existsAndHasRole(leadTechnologistId, INNOVATION_LEAD)).thenReturn(true);
        when(milestoneRestService.updateMilestone(any(MilestoneResource.class))).thenReturn(restSuccess());
        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(any(), any())).thenReturn(restSuccess(getMilestoneList().get(0)));

        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(competition.getExecutive(), executiveUserId);
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(competition.getLeadTechnologist(), leadTechnologistId);
        Set<Long> expectedInnovationAreaIds = asSet(innovationAreaId);
        Set<Long> actualInnovationAreaIds = competition.getInnovationAreas().stream().collect(Collectors.toSet());
        assertEquals(expectedInnovationAreaIds, actualInnovationAreaIds);
        assertEquals(competition.getInnovationSector(), innovationSectorId);
        assertEquals(competition.getCompetitionType(), competitionTypeId);
        assertEquals(innovationSectorId, competition.getInnovationSector());

        verify(competitionSetupRestService).updateCompetitionInitialDetails(competition);
        verify(competitionSetupRestService, never()).initApplicationForm(competition.getId(), competitionSetupForm.getCompetitionTypeId());
    }

}
