package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormValidatorBuilder.newFormValidator;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
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
    private AssessmentInviteRepository assessmentInviteRepository;
    @Mock
    private FormInputRepository formInputRepository;
    @Mock
    private PublicContentRepository publicContentRepository;
    @Mock
    private MilestoneRepository milestoneRepository;
    @Mock
    private QuestionRepository questionRepository;
	@Mock
	private SectionRepository sectionRepository;
	@Mock
	private CompetitionMapper competitionMapperMock;
	@Mock
	private CompetitionFunderService competitionFunderService;
	@Mock
	private AssessmentParticipantRepository assessmentParticipantRepository;
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

        Map<CompetitionSetupSection, Optional<Boolean>> resultMap = service.getSectionStatuses(competitionId).getSuccess();

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
		verify(assessmentParticipantRepository, never()).getByCompetitionIdAndUserIdAndRole(competitionId, existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		verify(assessmentParticipantRepository, never()).delete(Mockito.any(AssessmentParticipant.class));
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);

		AssessmentParticipant savedCompetitionParticipant = new AssessmentParticipant();
		savedCompetitionParticipant.setProcess(competition);
		savedCompetitionParticipant.setUser(leadTechnologist);
		savedCompetitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
		savedCompetitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

		// Verify that the correct CompetitionParticipant is saved
		verify(assessmentParticipantRepository).save(savedCompetitionParticipant);
	}

	@Test
	public void updateCompetitionInitialDetailsWhenExistingLeadTechnologistExists() {

		Long competitionId = 1L;
		Long existingLeadTechnologistId = 5L;
		Long newLeadTechnologistId = 7L;
		User leadTechnologist = UserBuilder.newUser().withId(newLeadTechnologistId).build();

		AssessmentParticipant competitionParticipant = newAssessmentParticipant().build();
		CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
				.withId(competitionId)
				.withLeadTechnologist(newLeadTechnologistId)
				.build();
		Competition competition = CompetitionBuilder.newCompetition()
				.withId(competitionId)
				.withLeadTechnologist(leadTechnologist)
				.build();
		when(assessmentParticipantRepository.getByCompetitionIdAndUserIdAndRole(competitionId,
				existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipant);
		when(competitionMapperMock.mapToDomain(competitionResource)).thenReturn(competition);
		when(competitionMapperMock.mapToResource(competition)).thenReturn(competitionResource);
		when(competitionRepository.save(competition)).thenReturn(competition);

		ServiceResult<Void> result = service.updateCompetitionInitialDetails(competitionId, competitionResource, existingLeadTechnologistId);

		assertTrue(result.isSuccess());
		verify(assessmentParticipantRepository).getByCompetitionIdAndUserIdAndRole(competitionId, existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		// Verify that the correct CompetitionParticipant is deleted
		verify(assessmentParticipantRepository).delete(competitionParticipant);
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);

		AssessmentParticipant savedCompetitionParticipant = new AssessmentParticipant();
		savedCompetitionParticipant.setProcess(competition);
		savedCompetitionParticipant.setUser(leadTechnologist);
		savedCompetitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
		savedCompetitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

		// Verify that the correct CompetitionParticipant is saved
		verify(assessmentParticipantRepository).save(savedCompetitionParticipant);
	}

	@Test
	public void updateCompetitionInitialDetailsWhenNewLeadTechnologistAlreadyExists() {

		Long competitionId = 1L;
		Long existingLeadTechnologistId = 5L;
		Long newLeadTechnologistId = 7L;

		AssessmentParticipant competitionParticipant = newAssessmentParticipant().build();
		CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
				.withId(competitionId)
				.withLeadTechnologist(newLeadTechnologistId)
				.build();
		Competition competition = CompetitionBuilder.newCompetition()
				.withId(competitionId)
				.withLeadTechnologist(UserBuilder.newUser().withId(newLeadTechnologistId).build())
				.build();
		AssessmentParticipant newLeadTechCompetitionParticipant = newAssessmentParticipant().withId(11L).build();
		when(assessmentParticipantRepository.getByCompetitionIdAndUserIdAndRole(competitionId,
				existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipant);
		when(competitionMapperMock.mapToDomain(competitionResource)).thenReturn(competition);
		when(competitionMapperMock.mapToResource(competition)).thenReturn(competitionResource);
		when(competitionRepository.save(competition)).thenReturn(competition);
		when(assessmentParticipantRepository.getByCompetitionIdAndUserIdAndRole(1L, newLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(newLeadTechCompetitionParticipant);

		ServiceResult<Void> result = service.updateCompetitionInitialDetails(competitionId, competitionResource, existingLeadTechnologistId);

		assertTrue(result.isSuccess());
		verify(assessmentParticipantRepository).getByCompetitionIdAndUserIdAndRole(competitionId, existingLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		verify(assessmentParticipantRepository).delete(competitionParticipant);
		verify(competitionFunderService).reinsertFunders(competitionResource);
		verify(competitionRepository).save(competition);
		verify(assessmentParticipantRepository).getByCompetitionIdAndUserIdAndRole(1L, newLeadTechnologistId, CompetitionParticipantRole.INNOVATION_LEAD);
		verify(assessmentParticipantRepository, never()).save(Mockito.any(AssessmentParticipant.class));
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


    @Test
    public void deleteCompetition() throws Exception {
        Competition competition = newCompetition()
                .withSections(newSection()
                        .withQuestions(newQuestion()
                                .withFormInputs(newFormInput()
                                        .withInputValidators(newFormValidator().buildSet(2))
                                        .build(1))
                                .build(1))
                        .build(1))
                .build();

        PublicContent publicContent = newPublicContent().build();

        when(competitionRepository.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepository.countByCompetitionIdAndStatusIn(competition.getId(), EnumSet.allOf
                (InviteStatus.class))).thenReturn(0);
        when(publicContentRepository.findByCompetitionId(competition.getId())).thenReturn(publicContent);

        ServiceResult<Void> result = service.deleteCompetition(competition.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(competitionRepository, assessmentInviteRepository, publicContentRepository, milestoneRepository);
        inOrder.verify(competitionRepository).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepository).countByCompetitionIdAndStatusIn(competition.getId(),
                EnumSet.allOf(InviteStatus.class));
        inOrder.verify(publicContentRepository).findByCompetitionId(competition.getId());
        inOrder.verify(publicContentRepository).delete(publicContent);
        // Test that the competition is saved without the form validators, deleting them
        inOrder.verify(competitionRepository).save(createCompetitionExpectationsWithoutFormValidators(competition));
        inOrder.verify(milestoneRepository).deleteByCompetitionId(competition.getId());
        inOrder.verify(competitionRepository).delete(competition);
        inOrder.verifyNoMoreInteractions();
    }

    private Competition createCompetitionExpectationsWithoutFormValidators(Competition competition) {
        return createLambdaMatcher(comp -> {
            assertEquals(competition.getId(), comp.getId());
            comp.getSections().forEach(section ->
                    section.getQuestions().forEach(question -> {
                        question.getFormInputs().forEach(formInput ->
                                assertTrue(formInput.getFormValidators().isEmpty()));
                    }));
        });
    }

    @Test
    public void deleteCompetition_assessmentInvitesExist() throws Exception {
        Competition competition = newCompetition().build();

        when(competitionRepository.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepository.countByCompetitionIdAndStatusIn(competition.getId(), EnumSet.allOf
                (InviteStatus.class))).thenReturn(1);

        ServiceResult<Void> result = service.deleteCompetition(competition.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED));

        InOrder inOrder = inOrder(competitionRepository, assessmentInviteRepository, publicContentRepository);
        inOrder.verify(competitionRepository).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepository).countByCompetitionIdAndStatusIn(competition.getId(),
                EnumSet.allOf(InviteStatus.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteCompetition_competitionNotFound() throws Exception {
        Competition competition = newCompetition().build();

        when(competitionRepository.findOne(competition.getId())).thenReturn(null);

        ServiceResult<Void> result = service.deleteCompetition(competition.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Competition.class, competition.getId())));

        verify(competitionRepository).findOne(competition.getId());
        verifyNoMoreInteractions(competitionRepository);
    }
}
