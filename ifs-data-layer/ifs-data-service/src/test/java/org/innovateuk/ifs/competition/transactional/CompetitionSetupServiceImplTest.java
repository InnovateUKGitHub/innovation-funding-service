package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupServiceImplTest {

	@InjectMocks
	private CompetitionSetupServiceImpl service;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private CompetitionTypeRepository competitionTypeRepository;
    @Mock
    private FormInputRepository formInputRepository;
    @Mock
    private QuestionRepository questionRepository;
	@Mock
	private SectionRepository sectionRepository;
	@Mock
	private GuidanceRowRepository assessmentScoreRowRepository;
	@Mock
	private AssessorCountOptionRepository competitionTypeAssessorOptionRepository;
	@Mock
	private EntityManager entityManager;
	@Mock
	private CompetitionMapper competitionMapperMock;
	@Mock
	private CompetitionFunderService competitionFunderService;
	@Mock
	private CompetitionParticipantRepository competitionParticipantRepository;
	@Mock
	private CompetitionSetupTemplateService competitionSetupTemplateService;

    @Before
	public void setup() {
        when(formInputRepository.findByCompetitionId(anyLong())).thenReturn(new ArrayList());
        when(questionRepository.findByCompetitionId(anyLong())).thenReturn(new ArrayList());
        when(sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(anyLong())).thenReturn(new ArrayList());
    }

    @Test
    public void copyFromCompetitionTypeTemplate() {
		long typeId = 4L;
		long competitionId = 2L;
    	Competition competitionTemplate = newCompetition().build();
		when(competitionSetupTemplateService.createCompetitionByCompetitionTemplate(competitionId, typeId)).thenReturn(ServiceResult.serviceSuccess(competitionTemplate));

		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);
    	assertTrue(result.isSuccess());
    }

	@Test
	public void updateCompetitionInitialDetailsWhenExistingLeadTechnologistDoesNotExist() {

		Long competitionId = 1L;
		Long existingLeadTechnologistId = null;
		Long newLeadTechnologistId = 7L;
		User leadTechnologist = UserBuilder.newUser().withId(newLeadTechnologistId).build();

		CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
				.withId(1L)
				.withLeadTechnologist(newLeadTechnologistId)
				.build();
		Competition competition = CompetitionBuilder.newCompetition()
				.withId(competitionId)
				.withLeadTechnologist(leadTechnologist)
				.build();
		when(competitionMapperMock.mapToDomain(competitionResource)).thenReturn(competition);
		when(competitionMapperMock.mapToResource(competition)).thenReturn(competitionResource);
		when(competitionRepository.save(competition)).thenReturn(competition);

		ServiceResult<Void> result = service.updateCompetitionInitialDetails(competitionId, competitionResource, existingLeadTechnologistId);

		assertTrue(result.isSuccess());
		verify(competitionParticipantRepository, never()).getByCompetitionIdAndUserIdAndRole(competitionId, existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		verify(competitionParticipantRepository, never()).delete(Mockito.any(CompetitionParticipant.class));
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);

		CompetitionParticipant savedCompetitionParticipant = new CompetitionParticipant();
		savedCompetitionParticipant.setProcess(competition);
		savedCompetitionParticipant.setUser(leadTechnologist);
		savedCompetitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
		savedCompetitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

		// Verify that the correct CompetitionParticipant is saved
		verify(competitionParticipantRepository).save(savedCompetitionParticipant);
	}

	@Test
	public void updateCompetitionInitialDetailsWhenExistingLeadTechnologistExists() {

		Long competitionId = 1L;
		Long existingLeadTechnologistId = 5L;
		Long newLeadTechnologistId = 7L;
		User leadTechnologist = UserBuilder.newUser().withId(newLeadTechnologistId).build();

		CompetitionParticipant competitionParticipant = CompetitionParticipantBuilder.newCompetitionParticipant().build();
		CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
				.withId(competitionId)
				.withLeadTechnologist(newLeadTechnologistId)
				.build();
		Competition competition = CompetitionBuilder.newCompetition()
				.withId(competitionId)
				.withLeadTechnologist(leadTechnologist)
				.build();
		when(competitionParticipantRepository.getByCompetitionIdAndUserIdAndRole(competitionId,
				existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipant);
		when(competitionMapperMock.mapToDomain(competitionResource)).thenReturn(competition);
		when(competitionMapperMock.mapToResource(competition)).thenReturn(competitionResource);
		when(competitionRepository.save(competition)).thenReturn(competition);

		ServiceResult<Void> result = service.updateCompetitionInitialDetails(competitionId, competitionResource, existingLeadTechnologistId);

		assertTrue(result.isSuccess());
		verify(competitionParticipantRepository).getByCompetitionIdAndUserIdAndRole(competitionId, existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		// Verify that the correct CompetitionParticipant is deleted
		verify(competitionParticipantRepository).delete(competitionParticipant);
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);

		CompetitionParticipant savedCompetitionParticipant = new CompetitionParticipant();
		savedCompetitionParticipant.setProcess(competition);
		savedCompetitionParticipant.setUser(leadTechnologist);
		savedCompetitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
		savedCompetitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

		// Verify that the correct CompetitionParticipant is saved
		verify(competitionParticipantRepository).save(savedCompetitionParticipant);
	}

	@Test
	public void updateCompetitionInitialDetailsWhenNewLeadTechnologistAlreadyExists() {

		Long competitionId = 1L;
		Long existingLeadTechnologistId = 5L;
		Long newLeadTechnologistId = 7L;

		CompetitionParticipant competitionParticipant = CompetitionParticipantBuilder.newCompetitionParticipant().build();
		CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
				.withId(competitionId)
				.withLeadTechnologist(newLeadTechnologistId)
				.build();
		Competition competition = CompetitionBuilder.newCompetition()
				.withId(competitionId)
				.withLeadTechnologist(UserBuilder.newUser().withId(newLeadTechnologistId).build())
				.build();
		CompetitionParticipant newLeadTechCompetitionParticipant = CompetitionParticipantBuilder.newCompetitionParticipant().withId(11L).build();
		when(competitionParticipantRepository.getByCompetitionIdAndUserIdAndRole(competitionId,
				existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipant);
		when(competitionMapperMock.mapToDomain(competitionResource)).thenReturn(competition);
		when(competitionMapperMock.mapToResource(competition)).thenReturn(competitionResource);
		when(competitionRepository.save(competition)).thenReturn(competition);
		when(competitionParticipantRepository.getByCompetitionIdAndUserIdAndRole(1L, newLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(newLeadTechCompetitionParticipant);

		ServiceResult<Void> result = service.updateCompetitionInitialDetails(competitionId, competitionResource, existingLeadTechnologistId);

		assertTrue(result.isSuccess());
		verify(competitionParticipantRepository).getByCompetitionIdAndUserIdAndRole(competitionId, existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		verify(competitionParticipantRepository).delete(competitionParticipant);
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);
		verify(competitionParticipantRepository).getByCompetitionIdAndUserIdAndRole(1L, newLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		verify(competitionParticipantRepository, never()).save(Mockito.any(CompetitionParticipant.class));
	}

	@Test
	public void testMarkAsSetup() {
		Long competitionId = 1L;
		Competition comp = new Competition();
		when(competitionRepository.findById(competitionId)).thenReturn(comp);

		service.markAsSetup(competitionId);

		assertTrue(comp.getSetupComplete());
	}

	@Test
	public void testReturnToSetup() {
		Long competitionId = 1L;
		Competition comp = new Competition();
		when(competitionRepository.findById(competitionId)).thenReturn(comp);

		service.returnToSetup(competitionId);

		assertFalse(comp.getSetupComplete());
	}
}
