package com.worth.ifs.competition.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.mapper.MilestoneMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.MilestoneRepository;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static com.worth.ifs.competition.resource.MilestoneType.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MilestoneServiceImplTest extends BaseServiceUnitTest<MilestoneServiceImpl>{
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
		when(milestoneMapper.mapToDomain(any(MilestoneResource.class))).thenAnswer(new Answer<Milestone>(){
			@Override
			public Milestone answer(InvocationOnMock invocation) throws Throwable {
				MilestoneResource arg = invocation.getArgumentAt(0, MilestoneResource.class);
                Milestone milestone = newMilestone().withType(arg.getType()).withDate(arg.getDate()).build();
				return milestone;
			}
		});
	}
	
	@Test
	public void testUpdateMilestones() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = newMilestoneResource()
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(LocalDateTime.of(2050, 3, 11, 0, 0), LocalDateTime.of(2050, 3, 10, 0, 0))
                .build(2);

		ServiceResult<Void> result = service.updateMilestones(1L, milestones);
		
		assertTrue(result.isSuccess());
		assertEquals(ASSESSMENT_PANEL, competition.getMilestones().get(0).getType());
		assertEquals(LocalDateTime.of(2050, 3, 10, 0, 0), competition.getMilestones().get(0).getDate());
		assertEquals(FUNDERS_PANEL, competition.getMilestones().get(1).getType());
		assertEquals(LocalDateTime.of(2050, 3, 11, 0, 0), competition.getMilestones().get(1).getDate());
	}
	
	@Test
	public void testUpdateNotSequential() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = newMilestoneResource()
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(LocalDateTime.of(2050, 3, 10, 0, 0), LocalDateTime.of(2050, 3, 11, 0, 0))
                .build(2);

		ServiceResult<Void> result = service.updateMilestones(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.nonsequential", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}
	
	@Test
	public void testUpdateMilestonesNullDate() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = newMilestoneResource()
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(LocalDateTime.of(2050, 3, 11, 0, 0), null)
                .build(2);

		ServiceResult<Void> result = service.updateMilestones(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.nulldate", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}
	
	@Test
	public void testUpdateMilestonesDateInPast() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = newMilestoneResource()
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL)
                .withDate(LocalDateTime.of(2050, 3, 11, 0, 0), LocalDateTime.of(1985, 3, 10, 0, 0))
                .build(2);

		ServiceResult<Void> result = service.updateMilestones(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.pastdate", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}
	
	@Test
	public void testUpdateMilestonesErrorsNotRepeated() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = newMilestoneResource()
                .withType(FUNDERS_PANEL, ASSESSMENT_PANEL, ALLOCATE_ASSESSORS, ASSESSOR_ACCEPTS)
                .build(4);

		ServiceResult<Void> result = service.updateMilestones(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.nulldate", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}

	@Test
	public void updateMilestone() {
		LocalDateTime milestoneDate = LocalDateTime.now();

		ServiceResult<Void> result = service.updateMilestone(newMilestoneResource().withType(MilestoneType.BRIEFING_EVENT).withDate(milestoneDate.plusMonths(1)).build());
		assertTrue(result.isSuccess());
	}

	@Test
	public void getAllMilestones() {
		List<Milestone> milestones = newMilestone().withType(MilestoneType.BRIEFING_EVENT, MilestoneType.LINE_DRAW, MilestoneType.NOTIFICATIONS).build(3);
		when(milestoneRepository.findAllByCompetitionId(1L)).thenReturn(milestones);

		ServiceResult<List<MilestoneResource>> result = service.getAllMilestonesByCompetitionId(1L);

		assertTrue(result.isSuccess());
		assertNotNull(result);
		assertEquals(3, milestones.size());
	}

	@Test
	public void getMilestoneByTypeAndCompetition() {
        Milestone milestone = newMilestone().withType(NOTIFICATIONS).build();
		when(milestoneRepository.findByTypeAndCompetitionId(NOTIFICATIONS, 1L)).thenReturn(milestone);

		ServiceResult<MilestoneResource> result = service.getMilestoneByTypeAndCompetitionId(MilestoneType.NOTIFICATIONS, 1L);
		assertTrue(result.isSuccess());
		assertEquals(MilestoneType.NOTIFICATIONS, milestone.getType());
		assertNull(milestone.getDate());
	}

	@Override
	protected MilestoneServiceImpl supplyServiceUnderTest() { return new MilestoneServiceImpl(); }
}
