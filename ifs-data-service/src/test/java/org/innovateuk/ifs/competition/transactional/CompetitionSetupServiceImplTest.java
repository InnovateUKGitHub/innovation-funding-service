package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessorCountOption;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.competition.builder.AssessorCountOptionBuilder.newAssessorCountOption;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
				).build();

		competitionType.setTemplate(competitionTemplate);

		when(competitionRepository.findById(competitionId)).thenReturn(competition);
		when(competitionTypeRepository.findOne(typeId)).thenReturn(competitionType);
		when(competitionTypeAssessorOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(typeId)).thenReturn(Optional.empty());

		ServiceResult<Void> result = service.copyFromCompetitionTypeTemplate(competitionId, typeId);

		assertTrue(result.isSuccess());
		assertEquals(competitionType, competition.getCompetitionType());
		assertEquals(competitionTemplate.getSections(), competition.getSections());
		Section section = competition.getSections().get(0);
		Question question = section.getQuestions().get(0);
		assertFalse(question.getFormInputs().get(0).getActive());
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
