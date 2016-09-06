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
				Milestone milestone = new Milestone();
				milestone.setType(arg.getType());
				milestone.setDate(arg.getDate());
				return milestone;
			}
		});
	}
	
	@Test
	public void testUpdateMilestones() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = asList(
                milestone(MilestoneType.FUNDERS_PANEL, LocalDateTime.of(2050, 3, 11, 0, 0)),
                milestone(MilestoneType.ASSESSMENT_PANEL, LocalDateTime.of(2050, 3, 10, 0, 0))
			);
		
		ServiceResult<Void> result = service.update(1L, milestones);
		
		assertTrue(result.isSuccess());
		assertEquals(MilestoneType.ASSESSMENT_PANEL, competition.getMilestones().get(0).getType());
		assertEquals(LocalDateTime.of(2050, 3, 10, 0, 0), competition.getMilestones().get(0).getDate());
		assertEquals(MilestoneType.FUNDERS_PANEL, competition.getMilestones().get(1).getType());
		assertEquals(LocalDateTime.of(2050, 3, 11, 0, 0), competition.getMilestones().get(1).getDate());
	}
	
	@Test
	public void testUpdateNotSequential() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = asList(
                milestone(MilestoneType.FUNDERS_PANEL, LocalDateTime.of(2050, 3, 10, 0, 0)),
                milestone(MilestoneType.ASSESSMENT_PANEL, LocalDateTime.of(2050, 3, 11, 0, 0))
			);
		
		ServiceResult<Void> result = service.update(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.nonsequential", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}
	
	@Test
	public void testUpdateMilestonesNullDate() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = asList(
                milestone(MilestoneType.FUNDERS_PANEL, LocalDateTime.of(2050, 3, 11, 0, 0)),
                milestone(MilestoneType.ASSESSMENT_PANEL, null)
			);
		
		ServiceResult<Void> result = service.update(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.nulldate", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}
	
	@Test
	public void testUpdateMilestonesDateInPast() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = asList(
                milestone(MilestoneType.FUNDERS_PANEL, LocalDateTime.of(2050, 3, 11, 0, 0)),
                milestone(MilestoneType.ASSESSMENT_PANEL, LocalDateTime.of(1985, 3, 10, 0, 0))
			);
		
		ServiceResult<Void> result = service.update(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.pastdate", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}
	
	@Test
	public void testUpdateMilestonesErrorsNotRepeated() {
		Competition competition = newCompetition().build();
		when(competitionRepository.findById(1L)).thenReturn(competition);
		
		List<MilestoneResource> milestones = asList(
                milestone(MilestoneType.FUNDERS_PANEL, null),
                milestone(MilestoneType.ASSESSMENT_PANEL, null),
                milestone(MilestoneType.ALLOCATE_ASSESSORS, null),
                milestone(MilestoneType.ASSESSOR_ACCEPTS, null)
			);
		
		ServiceResult<Void> result = service.update(1L, milestones);
		
		assertFalse(result.isSuccess());
		assertEquals(1, result.getFailure().getErrors().size());
		assertEquals("error.milestone.nulldate", result.getFailure().getErrors().get(0).getErrorKey());
		assertEquals(0, competition.getMilestones().size());
	}

	private MilestoneResource milestone(MilestoneType type, LocalDateTime date) {
		MilestoneResource resource = new MilestoneResource();
		resource.setType(type);
		resource.setDate(date);
		return resource;
	}

	@Override
	protected MilestoneServiceImpl supplyServiceUnderTest() { return new MilestoneServiceImpl(); }
}
