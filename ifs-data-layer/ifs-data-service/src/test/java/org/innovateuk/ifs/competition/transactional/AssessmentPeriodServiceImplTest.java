package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.MilestoneMapper;
import org.innovateuk.ifs.competition.repository.AssessmentPeriodRepository;
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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class AssessmentPeriodServiceImplTest extends BaseServiceUnitTest<AssessmentPeriodServiceImpl> {

    @InjectMocks
    private AssessmentPeriodServiceImpl service;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private MilestoneRepository milestoneRepository;
    @Mock
    private MilestoneMapper milestoneMapper;
    @Mock
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Before
    public void setUp() {

        when(competitionRepository.findById(1L))
                .thenReturn(Optional.of(newCompetition()
                        .withId(1L)
                        .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                        .withNonIfs(false)
                        .withAlwaysOpen(false)
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
    public void testUpdateAssessmentPeriodMilestonesNonSequential() {
        Competition competition = newCompetition().withAlwaysOpen(true).build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        AssessmentPeriod assessmentPeriod = new AssessmentPeriod(competition, 1);
        assessmentPeriod.setId(1L);

        ZonedDateTime briefingDate = ZonedDateTime.of(2050, 4, 11, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime acceptanceDate = ZonedDateTime.of(2050, 3, 10, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2050, 5, 10, 0, 0, 0, 0, ZoneId.systemDefault());
        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE)
                .withAssessmentPeriod(assessmentPeriod.getId())
                .withDate(briefingDate, acceptanceDate, deadlineDate)
                .build(3);

        List<Milestone> milestonesToSave = newMilestone()
                .withCompetition(newCompetition().withAlwaysOpen(true).withId(1L).build(),
                        newCompetition().withAlwaysOpen(true).withId(1L).build())
                .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE)
                .withAssessmentPeriod(assessmentPeriod)
                .withDate(acceptanceDate, acceptanceDate, deadlineDate)
                .build(3);

        when(milestoneMapper.mapToDomain(milestones)).thenReturn(milestonesToSave);

        ServiceResult<Void> result = service.updateAssessmentPeriodMilestones(milestones);

        assertFalse(result.isSuccess());
        verify(milestoneRepository, times(0)).saveAll(milestonesToSave);
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("error.milestone.nonsequential", result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void testUpdateAssessmentPeriodMilestones() {
        Competition competition = newCompetition().withAlwaysOpen(true).build();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        AssessmentPeriod assessmentPeriod = new AssessmentPeriod(competition, 1);
        assessmentPeriod.setId(1L);

        ZonedDateTime briefingDate = ZonedDateTime.of(2050, 3, 11, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime acceptanceDate = ZonedDateTime.of(2050, 4, 10, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2050, 5, 10, 0, 0, 0, 0, ZoneId.systemDefault());
        List<MilestoneResource> milestones = newMilestoneResource()
                .withCompetitionId(1L, 1L)
                .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE)
                .withAssessmentPeriod(assessmentPeriod.getId())
                .withDate(briefingDate, acceptanceDate, deadlineDate)
                .build(3);

        List<Milestone> milestonesToSave = newMilestone()
                .withCompetition(newCompetition().withAlwaysOpen(true).withId(1L).build(),
                        newCompetition().withAlwaysOpen(true).withId(1L).build())
                .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE)
                .withAssessmentPeriod(assessmentPeriod)
                .withDate(acceptanceDate, acceptanceDate, deadlineDate)
                .build(3);

        when(milestoneMapper.mapToDomain(milestones)).thenReturn(milestonesToSave);

        ServiceResult<Void> result = service.updateAssessmentPeriodMilestones(milestones);

        assertTrue(result.isSuccess());
        verify(milestoneRepository, times(1)).saveAll(milestonesToSave);
    }

    @Test
    public void testCreateAssessmentPeriodMilestones() {

        Competition competition = newCompetition().build();
        when(competitionRepository.findById(competition.getId()))
                .thenReturn(Optional.of(competition));
        AssessmentPeriod assessmentPeriod = new AssessmentPeriod();
        assessmentPeriod.setCompetition(competition);
        assessmentPeriod.setIndex(2);
        when(assessmentPeriodRepository.findAllByCompetitionId(competition.getId()))
                .thenReturn(Collections.singletonList(assessmentPeriod));

        ServiceResult<List<MilestoneResource>> result = service.createAssessmentPeriodMilestones(1L);
        assertTrue(result.isSuccess());
        verify(milestoneRepository).saveAll(anyList());
    }

    @Override
    protected AssessmentPeriodServiceImpl supplyServiceUnderTest() {
        return new AssessmentPeriodServiceImpl();
    }
}