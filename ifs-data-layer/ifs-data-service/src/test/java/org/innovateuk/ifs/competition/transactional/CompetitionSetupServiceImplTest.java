package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.assessment.builder.CompetitionAssessmentParticipantBuilder.newCompetitionAssessmentParticipant;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.junit.Assert.*;
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
	@Mock
    private SetupStatusService setupStatusService;

    @Before
	public void setup() {
        when(formInputRepository.findByCompetitionId(anyLong())).thenReturn(new ArrayList());
        when(questionRepository.findByCompetitionId(anyLong())).thenReturn(new ArrayList());
        when(sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(anyLong())).thenReturn(new ArrayList());
    }

    @Test
	public void testGetSectionStatuses() {
        final Long competitionId = 6939L;

        when(setupStatusService.findByTargetClassNameAndTargetId(Competition.class.getName(), competitionId))
                .thenReturn(ServiceResult.serviceSuccess(newSetupStatusResource().withId(23L)
                        .withTargetClassName(Competition.class.getName())
                        .withTargetId(competitionId)
                        .withClassPk(INITIAL_DETAILS.getId())
                        .withClassName(INITIAL_DETAILS.getClass().getName())
                        .withCompleted(Boolean.TRUE)
                        .build(1)));

        Map<CompetitionSetupSection, Optional<Boolean>> resultMap = service.getSectionStatuses(competitionId).getSuccessObjectOrThrowException();

        assertTrue(resultMap.containsKey(CompetitionSetupSection.HOME));
        assertEquals(Boolean.TRUE, resultMap.get(INITIAL_DETAILS).orElse(Boolean.FALSE));
        assertEquals(Boolean.FALSE, resultMap.get(APPLICATION_FORM).orElse(Boolean.FALSE));
    }

    @Test
    public void copyFromCompetitionTypeTemplate() {
		long typeId = 4L;
		long competitionId = 2L;
    	Competition competitionTemplate = newCompetition().build();
		when(competitionSetupTemplateService.initializeCompetitionByCompetitionTemplate(competitionId, typeId)).thenReturn(ServiceResult.serviceSuccess(competitionTemplate));

		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);
    	assertTrue(result.isSuccess());

    	verify(competitionSetupTemplateService, times(1)).initializeCompetitionByCompetitionTemplate(competitionId, typeId);
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
		verify(competitionParticipantRepository, never()).delete(Mockito.any(CompetitionAssessmentParticipant.class));
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);

		CompetitionAssessmentParticipant savedCompetitionParticipant = new CompetitionAssessmentParticipant();
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

		CompetitionAssessmentParticipant competitionParticipant = newCompetitionAssessmentParticipant().build();
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

		CompetitionAssessmentParticipant savedCompetitionParticipant = new CompetitionAssessmentParticipant();
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

		CompetitionAssessmentParticipant competitionParticipant = newCompetitionAssessmentParticipant().build();
		CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
				.withId(competitionId)
				.withLeadTechnologist(newLeadTechnologistId)
				.build();
		Competition competition = CompetitionBuilder.newCompetition()
				.withId(competitionId)
				.withLeadTechnologist(UserBuilder.newUser().withId(newLeadTechnologistId).build())
				.build();
		CompetitionAssessmentParticipant newLeadTechCompetitionParticipant = newCompetitionAssessmentParticipant().withId(11L).build();
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
		verify(competitionParticipantRepository, never()).save(Mockito.any(CompetitionAssessmentParticipant.class));
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

    @Test
    public void testMarkSectionCompleteFindOne() {
        final Long competitionId = 32L;
        final CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;
        final SetupStatusResource foundStatusResource = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withParentId(12L)
                .withCompleted(Boolean.FALSE).build();
        final SetupStatusResource savingStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withParentId(12L)
                .withCompleted(Boolean.TRUE).build();
        final SetupStatusResource savedStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withCompleted(Boolean.TRUE).build();

        when(setupStatusService.findSetupStatusAndTarget(section.getClass().getName(), section.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceSuccess(foundStatusResource));
        when(setupStatusService.saveSetupStatus(savingStatus)).thenReturn(serviceSuccess(savedStatus));

        service.markSectionComplete(competitionId, section);

        verify(setupStatusService, times(1)).findSetupStatusAndTarget(section.getClass().getName(), section.getId(), Competition.class.getName(), competitionId);
        verify(setupStatusService, times(1)).saveSetupStatus(savingStatus);
    }

	@Test
	public void testMarkSectionIncompleteCreateOne() {
        final Long competitionId = 32L;
        final CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;
        final SetupStatusResource savingStatus = newSetupStatusResource()
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();
        savingStatus.setId(null);
        final SetupStatusResource savedStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();

        when(setupStatusService.findSetupStatusAndTarget(section.getClass().getName(), section.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));
        when(setupStatusService.saveSetupStatus(savingStatus)).thenReturn(serviceSuccess(savedStatus));

        service.markSectionIncomplete(competitionId, section);

        verify(setupStatusService, times(1)).findSetupStatusAndTarget(section.getClass().getName(), section.getId(), Competition.class.getName(), competitionId);
        verify(setupStatusService, times(1)).saveSetupStatus(savingStatus);
	}

	@Test
	public void testMarkSubsectionCompleteFindOne() {
        final CompetitionSetupSubsection section = CompetitionSetupSubsection.APPLICATION_DETAILS;
        final Long competitionId = 32L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        final SetupStatusResource foundStatusResource = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withParentId(12L)
                .withCompleted(Boolean.FALSE).build();
        final SetupStatusResource savingStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withParentId(12L)
                .withCompleted(Boolean.TRUE).build();
        final SetupStatusResource savedStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withClassPk(competitionId)
                .withCompleted(Boolean.TRUE).build();
        final SetupStatusResource parentSectionStatus = newSetupStatusResource()
                .withId(12L)
                .withClassName(parentSection.getClass().getName())
                .withClassPk(parentSection.getId())
                .withParentId()
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();

        when(setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceSuccess(parentSectionStatus));
        when(setupStatusService.findSetupStatusAndTarget(section.getClass().getName(), section.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceSuccess(foundStatusResource));
        when(setupStatusService.saveSetupStatus(savingStatus)).thenReturn(serviceSuccess(savedStatus));

        service.markSubsectionComplete(competitionId, parentSection, section);

        verify(setupStatusService, times(1)).saveSetupStatus(savingStatus);
	}

	@Test
	public void testMarkSubsectionIncompleteCreateOne() {
        final CompetitionSetupSubsection section = CompetitionSetupSubsection.APPLICATION_DETAILS;
        final Long competitionId = 32L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        final SetupStatusResource savingStatus = newSetupStatusResource()
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();
        savingStatus.setId(null);
        final SetupStatusResource savedStatus = newSetupStatusResource()
                .withId(13L)
                .withClassName(section.getClass().getName())
                .withClassPk(section.getId())
                .withParentId(12L)
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();
        final SetupStatusResource parentSectionStatus = newSetupStatusResource()
                .withId(12L)
                .withClassName(parentSection.getClass().getName())
                .withClassPk(parentSection.getId())
                .withParentId()
                .withTargetClassName(Competition.class.getName())
                .withTargetId(competitionId)
                .withCompleted(Boolean.FALSE).build();

        when(setupStatusService.findSetupStatusAndTarget(parentSection.getClass().getName(), parentSection.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceSuccess(parentSectionStatus));
        when(setupStatusService.findSetupStatusAndTarget(section.getClass().getName(), section.getId(), Competition.class.getName(), competitionId))
                .thenReturn(serviceFailure(new Error("GENERAL_NOT_FOUND", HttpStatus.BAD_REQUEST)));
        when(setupStatusService.saveSetupStatus(savingStatus)).thenReturn(serviceSuccess(savedStatus));

        service.markSubsectionIncomplete(competitionId, parentSection, section);

        verify(setupStatusService, times(1)).saveSetupStatus(savingStatus);
	}
}
