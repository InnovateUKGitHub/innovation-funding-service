package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.domain.AssessorCountOption;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
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

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.assessment.builder.CompetitionAssessmentParticipantBuilder.newCompetitionAssessmentParticipant;
import static org.innovateuk.ifs.competition.builder.AssessorCountOptionBuilder.newAssessorCountOption;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
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

        Map<CompetitionSetupSection, Boolean> resultMap = service.getSectionStatuses(competitionId).getSuccessObjectOrThrowException();

        assertTrue(resultMap.containsKey(CompetitionSetupSection.HOME));
        assertEquals(Boolean.TRUE, resultMap.get(INITIAL_DETAILS));
        assertEquals(Boolean.FALSE, resultMap.get(APPLICATION_FORM));
    }

    @Test
    public void copyFromCompetitionTypeTemplate() {
		long typeId = 4L;
		long competitionId = 2L;
    	CompetitionType competitionType = newCompetitionType().withId(typeId).build();
    	Competition competition = newCompetition().build();
    	Competition competitionTemplate = newCompetition()
    			.withCompetitionType(competitionType)
    			.withSections(newSection()
						.withSectionType(SectionType.GENERAL)
						.withQuestions(newQuestion()
								.withFormInputs(newFormInput()
										.withGuidanceRows(newFormInputGuidanceRow().build(2)
										).build(2)
							).build(2)
					).build(2)
			).build();

		competitionType.setTemplate(competitionTemplate);

    	when(competitionRepository.findById(competitionId)).thenReturn(competition);
		when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
		when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.empty());

		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

    	assertTrue(result.isSuccess());
		assertEquals(competitionType, competition.getCompetitionType());
		assertEquals(competitionTemplate.getSections(), competition.getSections());
    }

	@Test
	public void copyFromSectorCompetitionTypeTemplate() {
		long typeId = 5L;
		long competitionId = 2L;
		CompetitionType competitionType = newCompetitionType()
				.withId(typeId)
				.withName("Sector")
				.build();
		Competition competition = newCompetition().build();
		Competition competitionTemplate = newCompetition()
				.withCompetitionType(competitionType)
				.withAcademicGrantPercentage(222)
				.withSections(newSection()
						.withSectionType(SectionType.GENERAL)
						.withQuestions(newQuestion()
								.withShortName("Scope")
								.withFormInputs(newFormInput()
										.withType(ASSESSOR_APPLICATION_IN_SCOPE)
										.withGuidanceRows(newFormInputGuidanceRow().build(2)
										).build(2)
								).build(1)
						).build(1)
				)
				.build();

		competitionType.setTemplate(competitionTemplate);

		when(competitionRepository.findById(competitionId)).thenReturn(competition);
		when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
		when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.empty());

		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

		assertTrue(result.isSuccess());
		assertEquals(competitionType, competition.getCompetitionType());
		assertEquals(competitionTemplate.getSections(), competition.getSections());
		assertEquals(competitionTemplate.getAcademicGrantPercentage(), competition.getAcademicGrantPercentage());
		Section section = competition.getSections().get(0);
		Question question = section.getQuestions().get(0);
		assertFalse(question.getFormInputs().get(0).getActive());
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
    public void testInitialiseFormWithSectionHierarchy() {
		Long typeId = 4L;
    	Section parent = newSection()
				.withName("parent")
				.build();

    	Section child1 = newSection()
				.withName("child1")
				.withParentSection(parent)
				.build();
		Section child2 = newSection()
				.withName("child2")
				.withParentSection(parent)
				.build();
    	parent.setChildSections(new ArrayList<>(asList(child1, child2)));

		CompetitionType competitionType = newCompetitionType().withId(typeId).build();
		Competition competition = newCompetition().build();
    	Competition competitionTemplate = newCompetition()
    			.withSections(asList(
    					parent, child1, child2
				))
    			.build();

		competitionType.setTemplate(competitionTemplate);
    	when(competitionRepository.findById(123L)).thenReturn(competition);
    	when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
		when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.empty());

		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(123L, 4L);

    	assertTrue(result.isSuccess());
    	assertEquals(3, competition.getSections().size());
    	Section parentSection = competition.getSections().get(0);
    	Section child1Section = competition.getSections().get(1);
    	Section child2Section = competition.getSections().get(2);
    	assertEquals("parent", parentSection.getName());
    	assertEquals("child1", child1Section.getName());
    	assertEquals("child2", child2Section.getName());
    	assertNull(parentSection.getParentSection());
    	assertEquals(2, parentSection.getChildSections().size());
    	assertTrue(parentSection.getChildSections().contains(child1Section));
    	assertTrue(parentSection.getChildSections().contains(child2Section));
    	assertEquals(parentSection, child1Section.getParentSection());
    	assertNull(child1Section.getChildSections());
    	assertEquals(parentSection, child2Section.getParentSection());
    	assertNull(child2Section.getChildSections());
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
	public void copyFromCompetitionTypeTemplateAssessorCountAndPay() {
		long typeId = 4L;
		long competitionId = 2L;
		CompetitionType competitionType = newCompetitionType().withId(typeId).build();
		Competition competition = newCompetition().build();
		Competition competitionTemplate = newCompetition()
				.withCompetitionType(competitionType)
				.withSections(newSection()
						.withSectionType(SectionType.GENERAL)
						.withQuestions(newQuestion()
								.withFormInputs(newFormInput()
										.build(2)
								).build(2)
						).build(2)
				).build();

		competitionType.setTemplate(competitionTemplate);

		AssessorCountOption assessorOption = newAssessorCountOption().withId(1L)
				.withAssessorOptionName("1").withAssessorOptionValue(1).withDefaultOption(Boolean.TRUE).build();

		when(competitionRepository.findById(competitionId)).thenReturn(competition);
		when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
		when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.of(assessorOption));
		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

		assertTrue(result.isSuccess());
		assertEquals(competition.getCompetitionType(), competitionType);
		assertEquals(Integer.valueOf(1), competition.getAssessorCount());
		assertEquals(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY, competition.getAssessorPay());
	}

	@Test
	public void copyFromCompetitionTypeTemplateAssessorCountAndPayWithNoDefault() {
		long typeId = 4L;
		long competitionId = 2L;
		CompetitionType competitionType = newCompetitionType().withId(typeId).build();
		Competition competition = newCompetition().build();
		Competition competitionTemplate = newCompetition()
				.withCompetitionType(competitionType)
				.withSections(newSection()
						.withSectionType(SectionType.GENERAL)
						.withQuestions(newQuestion()
								.withFormInputs(newFormInput()
										.build(2)
								).build(2)
						).build(2)
				).build();

		competitionType.setTemplate(competitionTemplate);

		when(competitionRepository.findById(competitionId)).thenReturn(competition);
		when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
		when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.empty());
		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

		assertTrue(result.isSuccess());
		assertNull(competition.getAssessorCount());
		assertEquals(CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY, competition.getAssessorPay());
	}
}
