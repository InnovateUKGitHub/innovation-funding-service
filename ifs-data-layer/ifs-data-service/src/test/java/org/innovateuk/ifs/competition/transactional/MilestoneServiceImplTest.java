package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.MilestoneMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MilestoneServiceImplTest extends BaseServiceUnitTest<MilestoneServiceImpl> {
    @InjectMocks
    private MilestoneServiceImpl service;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private MilestoneRepository milestoneRepository;
    @Mock
    private MilestoneMapper milestoneMapper;

    @Before
    public void setUp() {
        when(competitionRepository.findById(1L))
                .thenReturn(Optional.of(newCompetition()
                        .withId(1L)
                        .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                        .withNonIfs(false)
                        .build()));

        when(milestoneMapper.mapToDomain(any(MilestoneResource.class))).thenAnswer(new Answer<Milestone>() {
            @Override
            public Milestone answer(InvocationOnMock invocation) throws Throwable {
                MilestoneResource arg = invocation.getArgument(0);
                Milestone milestone = newMilestone().withType(arg.getType()).withDate(arg.getDate()).build();
                return milestone;
            }
        });
    }

    @Test
    public void testUpdateMilestones() {
        Competition competition = newCompetition().build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2050, 3, 10, 0, 0, 0, 0, ZoneId.systemDefault()))
                .build(2);

        List<Milestone> milestonesToSave = newMilestone()
                .withCompetition(newCompetition().withId(1L).build(), newCompetition().withId(1L).build())
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2050, 3, 10, 0, 0, 0, 0, ZoneId.systemDefault()))
                .build(2);

        when(milestoneMapper.mapToDomain(milestones)).thenReturn(milestonesToSave);

        ServiceResult<Void> result = service.updateMilestones(milestones);

        assertTrue(result.isSuccess());
        verify(milestoneRepository, times(1)).saveAll(milestonesToSave);
    }

    @Test
    public void testUpdateNotSequential() {
        Competition competition = newCompetition().build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.of(2050, 3, 10, 0, 0, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault()))
                .build(2);

        ServiceResult<Void> result = service.updateMilestones(milestones);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("error.milestone.nonsequential", result.getFailure().getErrors().get(0).getErrorKey());
        assertEquals(0, competition.getMilestones().size());
    }

    @Test
    public void testUpdateMilestonesNullDate() {
        Competition competition = newCompetition().build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault()), null)
                .build(2);

        ServiceResult<Void> result = service.updateMilestones(milestones);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("error.milestone.nulldate", result.getFailure().getErrors().get(0).getErrorKey());
        assertEquals(0, competition.getMilestones().size());
    }

    @Test
    public void testUpdateMilestonesDateInPast() {
        Competition competition = newCompetition().build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(1985, 3, 10, 0, 0, 0, 0, ZoneId.systemDefault()))
                .build(2);

        ServiceResult<Void> result = service.updateMilestones(milestones);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("error.milestone.pastdate", result.getFailure().getErrors().get(0).getErrorKey());
        assertEquals(0, competition.getMilestones().size());
    }

    @Test
    public void testUpdateMilestonesErrorsNotRepeated() {
        Competition competition = newCompetition().build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL, ALLOCATE_ASSESSORS, ASSESSOR_ACCEPTS)
                .build(4);

        ServiceResult<Void> result = service.updateMilestones(milestones);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("error.milestone.nulldate", result.getFailure().getErrors().get(0).getErrorKey());
        assertEquals(0, competition.getMilestones().size());
    }

    @Test
    public void updateMilestone() {
        ZonedDateTime milestoneDate = ZonedDateTime.now();

        ServiceResult<Void> result = service.updateMilestone(newMilestoneResource().withType(BRIEFING_EVENT).withDate(milestoneDate.plusMonths(1)).build());
        assertTrue(result.isSuccess());
    }

    @Test
    public void getAllMilestones() {
        List<Milestone> milestones = newMilestone().withType(BRIEFING_EVENT, LINE_DRAW, NOTIFICATIONS).build(3);
        when(milestoneRepository.findAllByCompetitionId(1L)).thenReturn(milestones);

        ServiceResult<List<MilestoneResource>> result = service.getAllMilestonesByCompetitionId(1L);

        assertTrue(result.isSuccess());
        assertNotNull(result);
        assertEquals(3, milestones.size());
    }

    @Test
    public void getAllPublicMilestones() {
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName("Competition Type").build()).build();
        List<Milestone> milestones = newMilestone().withType(OPEN_DATE, REGISTRATION_DATE, NOTIFICATIONS, SUBMISSION_DATE).build(4);
        when(milestoneRepository.findByCompetitionIdAndTypeIn(competition.getId(), asList(OPEN_DATE, REGISTRATION_DATE, SUBMISSION_DATE, NOTIFICATIONS)))
                .thenReturn(milestones);
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<List<MilestoneResource>> result = service.getAllPublicMilestonesByCompetitionId(competition.getId());

        assertTrue(result.isSuccess());
        assertNotNull(result);
        assertEquals(4, milestones.size());
    }

    @Test
    public void getAllPublicMilestones_horizon2020() {
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName("Horizon 2020").build()).build();
        List<Milestone> milestones = newMilestone().withType(OPEN_DATE, REGISTRATION_DATE).build(3);
        when(milestoneRepository.findByCompetitionIdAndTypeIn(competition.getId(), asList(OPEN_DATE, REGISTRATION_DATE, SUBMISSION_DATE)))
                .thenReturn(milestones);
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<List<MilestoneResource>> result = service.getAllPublicMilestonesByCompetitionId(competition.getId());

        assertTrue(result.isSuccess());
        assertNotNull(result);
        assertEquals(3, milestones.size());
    }

    @Test
    public void allPublicDatesCompleteSuccess() {
        List<Milestone> milestones = newMilestone().withType(OPEN_DATE, SUBMISSION_DATE, NOTIFICATIONS).withDate(ZonedDateTime.now()).build(4);
        when(milestoneRepository.findByCompetitionIdAndTypeIn(1L, asList(OPEN_DATE, SUBMISSION_DATE, NOTIFICATIONS)))
                .thenReturn(milestones);

        ServiceResult<Boolean> result = service.allPublicDatesComplete(1L);

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }


    @Test
    public void allPublicDatesCompleteSuccessForNonIfs() {
        when(competitionRepository.findById(1L))
                .thenReturn(Optional.of(newCompetition()
                        .withId(1L)
                        .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                        .withNonIfs(true)
                        .build()));

        List<Milestone> milestones = newMilestone().withType(OPEN_DATE, REGISTRATION_DATE, NOTIFICATIONS, SUBMISSION_DATE).withDate(ZonedDateTime.now()).build(4);
        when(milestoneRepository.findByCompetitionIdAndTypeIn(1L, asList(OPEN_DATE, REGISTRATION_DATE, SUBMISSION_DATE)))
                .thenReturn(milestones);

        ServiceResult<Boolean> result = service.allPublicDatesComplete(1L);

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }

    @Test
    public void allPublicDatesCompleteFailure() {
        List<Milestone> milestones = newMilestone().withType(RELEASE_FEEDBACK, SUBMISSION_DATE).build(2);
        when(milestoneRepository.findByCompetitionIdAndTypeIn(1L, asList(OPEN_DATE, SUBMISSION_DATE, NOTIFICATIONS)))
                .thenReturn(milestones);

        ServiceResult<Boolean> result = service.allPublicDatesComplete(1L);

        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void getMilestoneByTypeAndCompetition() {
        Milestone milestone = newMilestone().withType(NOTIFICATIONS).build();
        when(milestoneRepository.findByTypeAndCompetitionId(NOTIFICATIONS, 1L)).thenReturn(Optional.ofNullable(milestone));
        when(milestoneMapper.mapToResource(milestone)).thenReturn(newMilestoneResource().withType(NOTIFICATIONS).build());

        ServiceResult<MilestoneResource> result = service.getMilestoneByTypeAndCompetitionId(NOTIFICATIONS, 1L);
        assertTrue(result.isSuccess());
        assertEquals(NOTIFICATIONS, milestone.getType());
        assertNull(milestone.getDate());
    }

    @Test
    public void getMilestoneByTypeAndCompetitionReturnsNotFoundErrorWhenNotPresent() {
        when(milestoneRepository.findByTypeAndCompetitionId(NOTIFICATIONS, 1L)).thenReturn(Optional.empty());

        ServiceResult<MilestoneResource> result = service.getMilestoneByTypeAndCompetitionId(NOTIFICATIONS, 1L);
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateMilestones_milestonesForNonIfsCompetitionDoNotHaveToBeInFuture() {
        Competition nonIfsCompetition = newCompetition().withNonIfs(true).build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(nonIfsCompetition));

        ZonedDateTime lastYearSomewhere = ZonedDateTime.now()
                .minusYears(1);
        ZonedDateTime lastYearSomewhereButLater = ZonedDateTime.now()
                .minusYears(1)
                .plusDays(1);

        List<MilestoneResource> pastMilestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(lastYearSomewhereButLater, lastYearSomewhere)
                .build(2);

        ServiceResult<Void> result = service.updateMilestones(pastMilestones);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateMilestones_milestonesForIfsCompetitionHaveToBeInFuture() {
        Competition ifsCompetition = newCompetition().withNonIfs(false).build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(ifsCompetition));

        ZonedDateTime lastYearSomewhere = ZonedDateTime.now()
                .minusYears(1);
        ZonedDateTime lastYearSomewhereButLater = ZonedDateTime.now()
                .minusYears(1)
                .plusDays(1);

        List<MilestoneResource> pastMilestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(lastYearSomewhereButLater, lastYearSomewhere)
                .build(2);

        ServiceResult<Void> result = service.updateMilestones(pastMilestones);

        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0).getErrorKey(), "error.milestone.pastdate");
    }

    @Test
    public void testUpdateMilestones_whenMilestonesAreReferencingDifferentCompetitionsShouldReturnError() {
        Competition competition = newCompetition().build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 2L)
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2050, 3, 10, 0, 0, 0, 0, ZoneId.systemDefault()))
                .build(2);

        ServiceResult<Void> result = service.updateMilestones(milestones);

        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0).getErrorKey(), "error.title.status.400");
    }

    @Test
    public void updateCompletionStage_noChange() {

        Competition competition = newCompetition()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateCompletionStage(1L, CompetitionCompletionStage.PROJECT_SETUP);

        assertTrue(result.isSuccess());
        verifyNoMoreInteractions(milestoneRepository);
    }

    @Test
    @Rollback
    public void updateCompletionStage() {

        Competition competition = newCompetition()
                .withStartDate(ZonedDateTime.now())
                .build();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateCompletionStage(1L, CompetitionCompletionStage.PROJECT_SETUP);

        assertTrue(result.isSuccess());
    }

    @Override
    protected MilestoneServiceImpl supplyServiceUnderTest() {
        return new MilestoneServiceImpl();
    }
}
